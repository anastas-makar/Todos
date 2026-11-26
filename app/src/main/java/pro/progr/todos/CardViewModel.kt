package pro.progr.todos

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.progr.diagnostics.Diagnostics
import pro.progr.diagnostics.NoOpDiagnostics
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardTag
import pro.progr.todos.brightcards.model.CardAction
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.brightcards.ColorUpdateInterface
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.SublistChain
import pro.progr.todos.datefilters.FilterType
import java.util.concurrent.CopyOnWriteArrayList

class CardViewModel private constructor(
    private val repository: CardRepository,
    private val _cardContent: MutableStateFlow<CardContent>,
    private val isNewCard: Boolean,
    initialCardId: String? = null,
    private val diagnostics: Diagnostics = NoOpDiagnostics
) : ViewModel(), ColorUpdateInterface {

    internal val cardContent: MutableState<CardContent> = mutableStateOf(_cardContent.value.copy(id = initialCardId))
    val scheduleState: MutableState<Schedule> = mutableStateOf(
        Schedule(Schedule.Pattern(linkedMapOf(), FilterType.NO_TERMS))
    )
    private val _cardLoadState = MutableStateFlow(CardLoadState.LOADING)
    val cardLoadState: StateFlow<CardLoadState> = _cardLoadState
    private var requestedCardId: String? = null

    enum class CardLoadState { LOADING, READY, FAILED }

    // Action emitted when a card metadata control is tapped.
    private val _cardActionClicked = MutableStateFlow<CardAction?>(null)
    val cardActionClicked: Flow<CardAction?> = _cardActionClicked
    @Volatile private var pendingTitle: String? = null
    @Volatile private var pendingDescription: String? = null
    private var titleSaveJob: Job? = null
    private var descriptionSaveJob: Job? = null
    private val templateWriteJobs = CopyOnWriteArrayList<Job>()

    fun getCardId() : String? {
        return cardContent.value.id
    }

    fun onCardActionClicked(action: CardAction) {
        _cardActionClicked.value = action
    }

    fun onCardActionClickFired() {
        _cardActionClicked.value = null
    }

    constructor(
        repository: CardRepository,
        style: ColorStyle,
        diagnostics: Diagnostics = NoOpDiagnostics
    ) : this(
        repository,
        MutableStateFlow(CardContent(style = style)),
        true,
        null,
        diagnostics
    ) {
        saveCard(_cardContent.value)
    }

    constructor(
        repository: CardRepository,
        id: String,
        diagnostics: Diagnostics = NoOpDiagnostics
    ) : this(
        repository,
        MutableStateFlow(CardContent()),
        false,
        id,
        diagnostics
    ) {
        // Load the note once. The editor keeps its own state and does not collect DB emissions.
        requestedCardId = id
        getCard(id)
    }

    fun getCard(id: String) : Job {
        return viewModelScope.launch (Dispatchers.Default) {
            requestedCardId = id
            _cardLoadState.value = CardLoadState.LOADING
            Log.d("TodosCardEditor", "Loading card id=$id")
            try {
                val loaded = repository.getCard(id)
                _cardContent.value = loaded
                cardContent.value = loaded
                scheduleState.value = repository.getSchedule()
                _cardLoadState.value = CardLoadState.READY
                Log.d("TodosCardEditor", "Loaded card id=$id")
            } catch (e: CancellationException) {
                throw e
            } catch (t: Exception) {
                diagnostics.breadcrumb("todos_card_load: failed type=${t.javaClass.simpleName}")
                diagnostics.reportNonFatal(t, issueKey = "todos_card_load_failure")
                Log.e("TodosCardEditor", "Failed to load card id=$id", t)
                _cardLoadState.value = CardLoadState.FAILED
            }
        }
    }

    fun retryCardLoad() {
        requestedCardId?.let(::getCard)
    }

    fun deleteCard() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteCard(_cardContent.value)
        }
    }

    private fun saveCard(content: CardContent): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            repository.saveCard(content).also { savedCardContent ->
                _cardContent.value = savedCardContent
                cardContent.value = savedCardContent
                scheduleState.value = repository.getSchedule()
                _cardLoadState.value = CardLoadState.READY
            }
        }
    }

    fun updateCardTitle(title: String) {
        val content = updateVisibleContent { it.copy(title = title) } ?: return
        content.id?.let { queueTitleSave(it, title) }
    }

    fun updateCardDescription(description: String) {
        val content = updateVisibleContent { it.copy(text = description) } ?: return
        content.id?.let { queueDescriptionSave(it, description) }
    }

    override fun updateCardStyle(style: ColorStyle) {
        val content = updateVisibleContent { it.copy(style = style) } ?: return
        content.id?.let { id ->
            persist { repository.updateCardStyle(id, style) }
        }
    }

    override fun updateCardTitleBackground(filled: Boolean) {
        val content = updateVisibleContent { it.copy(fillTitleBackground = filled) } ?: return
        content.id?.let { id ->
            persist { repository.updateCardTitleBackground(id, filled) }
        }
    }

    override fun updateCardDescriptionBackground(filled: Boolean) {
        val content = updateVisibleContent { it.copy(fillTextBackground = filled) } ?: return
        content.id?.let { id ->
            persist { repository.updateCardDescriptionBackground(id, filled) }
        }
    }

    fun updateCardReward(reward: Int) {
        val content = updateVisibleContent { it.copy(reward = reward) } ?: return
        content.id?.let { id ->
            persist { repository.updateCardReward(id, reward) }
        }
    }

    fun updateCardTodo(todo: TodoStatus) {
        val content = updateVisibleContent { it.copy(todo = todo) } ?: return
        content.id?.let { id ->
            persist { repository.updateCardTodo(id, todo) }
        }
    }

    /** Update the in-memory editor snapshot and return it only when it changed. */
    private fun updateVisibleContent(transform: (CardContent) -> CardContent): CardContent? {
        val updated = transform(_cardContent.value)
        if (updated == _cardContent.value) return null
        _cardContent.value = updated
        cardContent.value = updated
        return updated
    }

    private fun queueTitleSave(noteId: String, title: String) {
        pendingTitle = title
        titleSaveJob?.cancel()
        titleSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(TEXT_SAVE_DEBOUNCE_MS)
            repository.enqueueTemplateEdit { repository.updateCardTitle(noteId, title) }.join()
            if (pendingTitle == title) pendingTitle = null
        }
    }

    private fun queueDescriptionSave(noteId: String, description: String) {
        pendingDescription = description
        descriptionSaveJob?.cancel()
        descriptionSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(TEXT_SAVE_DEBOUNCE_MS)
            repository.enqueueTemplateEdit { repository.updateCardDescription(noteId, description) }.join()
            if (pendingDescription == description) pendingDescription = null
        }
    }

    private fun persist(update: suspend () -> Unit): Job {
        templateWriteJobs.removeAll { !it.isActive }
        val job = repository.enqueueTemplateEdit(update)
        templateWriteJobs.add(job)
        return job
    }

    fun flushPendingCardEdits() {
        val id = _cardContent.value.id ?: return
        val title = pendingTitle
        val description = pendingDescription
        titleSaveJob?.cancel()
        descriptionSaveJob?.cancel()
        titleSaveJob = null
        descriptionSaveJob = null
        pendingTitle = null
        pendingDescription = null
        if (title != null || description != null) {
            persist {
                if (title != null) repository.updateCardTitle(id, title)
                if (description != null) repository.updateCardDescription(id, description)
            }
        }
    }

    suspend fun flushAndAwaitCardEdits() {
        flushPendingCardEdits()
        templateWriteJobs.toList().joinAll()
    }

    fun getSublistChane() : SublistChain {
        return repository.getSublistChane()
    }

    suspend fun setScheduleAndAwait(schedule: Schedule) {
        flushAndAwaitCardEdits()
        withContext(Dispatchers.IO) {
            repository.updateCardSchedule(schedule)
        }
        scheduleState.value = schedule
        val description = repository.getScheduleDescription()
        updateVisibleContent { it.copy(scheduleLabel = description) }
    }

    fun updateCardLabels(listName : String) {
        updateVisibleContent { it.copy(
            scheduleLabel = repository.getScheduleDescription(),
            listLabel = listName,
            tagsLabel = if (cardContent.value.tags.isEmpty()) repository.getNoTagsText()
                else cardContent.value.tags.fastJoinToString(separator = " #", prefix = "#")
        ) }
    }

    fun updateList(list : NList) {
        updateCardLabels(list.getName())

        viewModelScope.launch {
            flushAndAwaitCardEdits()
            withContext(Dispatchers.IO) {
                repository.updateList(list.sublistChain)
            }
        }
    }

    fun setDone() {
        viewModelScope.launch (Dispatchers.Default) {
            repository.setCardDone(_cardContent.value)
        }
    }

    fun showReward(): Boolean {
        return true
    }

    override fun getCardContent(): MutableState<CardContent> {
        return cardContent
    }

    fun inserAndAddTag(tagName: String) {
        viewModelScope.launch (Dispatchers.Default) {
            cardContent.value.id?.let { id ->
                val tag = repository.insertTag(tagName) ?: return@let
                if (_cardContent.value.tags.none { it.id == tag.id }) {
                    repository.addTag(tagId = tag.id, noteId = id)
                    val updated = _cardContent.value.copy(
                        tags = _cardContent.value.tags.plus(CardTag(tag.id, tag.title))
                    )
                    val labeled = updated.copy(
                        tagsLabel = updated.tags.fastJoinToString(separator = " #", prefix = "#")
                    )
                    _cardContent.value = labeled
                    cardContent.value = labeled
                }
            }
        }
    }

    fun addTag(tag: NoteTag) {
        viewModelScope.launch (Dispatchers.Default) {
            cardContent.value.id?.let { id ->
                repository.addTag(tagId = tag.id, noteId = id)
                val updated = _cardContent.value.copy(
                    tags = _cardContent.value.tags.plus(CardTag(tag.id, tag.title))
                )
                val labeled = updated.copy(
                    tagsLabel = updated.tags.fastJoinToString(separator = " #", prefix = "#")
                )
                _cardContent.value = labeled
                cardContent.value = labeled
            }
        }
    }

    fun removeNoteTag(tag: NoteTag) {
        viewModelScope.launch (Dispatchers.Default) {
            cardContent.value.id?.let { id ->
                repository.removeNoteTag(tagId = tag.id, noteId = id)
                val updated = _cardContent.value.copy(
                    tags = _cardContent.value.tags.filter {
                            cardTag -> cardTag.id != tag.id })
                val labeled = updated.copy(
                    tagsLabel = if (updated.tags.isEmpty()) repository.getNoTagsText()
                    else updated.tags.fastJoinToString(separator = " #", prefix = "#")
                )
                _cardContent.value = labeled
                cardContent.value = labeled
            }
        }
    }

    private companion object {
        const val TEXT_SAVE_DEBOUNCE_MS = 350L
    }
}
