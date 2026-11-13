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
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardTag
import pro.progr.todos.brightcards.model.CardAction
import pro.progr.todos.brightcards.vm.CardViewModel
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.SublistChain
import java.util.concurrent.CopyOnWriteArrayList

class CardViewModel private constructor(
    private val repository: CardRepository,
    private val _cardContent: MutableStateFlow<CardContent>,
    private val isNewCard: Boolean,
    initialCardId: String? = null
) : ViewModel(), CardViewModel {

    internal val cardContent: MutableState<CardContent> = mutableStateOf(_cardContent.value.copy(id = initialCardId))
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

    override fun getCardId() : String? {
        return cardContent.value.id
    }

    override fun onCardActionClicked(action: CardAction) {
        _cardActionClicked.value = action
    }

    override fun onCardActionClickFired() {
        _cardActionClicked.value = null
    }

    constructor(repository: CardRepository, style: ColorStyle) : this(
        repository,
        MutableStateFlow(CardContent(style = style)),
        true,
        null
    ) {
        saveCard(_cardContent.value)
    }

    constructor(repository: CardRepository, id: String) : this(
        repository,
        MutableStateFlow(CardContent()),
        false,
        id
    ) {
        // Load the note once. The editor keeps its own state and does not collect DB emissions.
        requestedCardId = id
        getCard(id)
    }

    override fun getCard(id: String) : Job {
        return viewModelScope.launch (Dispatchers.Default) {
            requestedCardId = id
            _cardLoadState.value = CardLoadState.LOADING
            Log.d("TodosCardEditor", "Loading card id=$id")
            try {
                val loaded = repository.getCard(id)
                _cardContent.value = loaded
                cardContent.value = loaded
                _cardLoadState.value = CardLoadState.READY
                Log.d("TodosCardEditor", "Loaded card id=$id")
            } catch (e: CancellationException) {
                throw e
            } catch (t: Exception) {
                Log.e("TodosCardEditor", "Failed to load card id=$id", t)
                _cardLoadState.value = CardLoadState.FAILED
            }
        }
    }

    fun retryCardLoad() {
        requestedCardId?.let(::getCard)
    }

    override fun deleteCard() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteCard(_cardContent.value)
        }
    }

    private fun saveCard(content: CardContent): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            repository.saveCard(content).also { savedCardContent ->
                _cardContent.value = savedCardContent
                cardContent.value = savedCardContent
                _cardLoadState.value = CardLoadState.READY
            }
        }
    }

    override fun updateCard(content: CardContent) {
        val previous = _cardContent.value
        // Update the visible form synchronously. Persist only fields that changed.
        cardContent.value = content
        _cardContent.value = content

        if (content.id == null) return
        val id = content.id ?: return

        if (previous.title != content.title) queueTitleSave(id, content.title)
        if (previous.text != content.text) queueDescriptionSave(id, content.text)
        if (previous.style != content.style) persist { repository.updateCardStyle(id, content.style, isNewCard) }
        if (previous.fillTitleBackground != content.fillTitleBackground) {
            persist { repository.updateCardTitleBackground(id, content.fillTitleBackground, isNewCard) }
        }
        if (previous.fillTextBackground != content.fillTextBackground) {
            persist { repository.updateCardDescriptionBackground(id, content.fillTextBackground, isNewCard) }
        }
        if (previous.reward != content.reward) persist { repository.updateCardReward(id, content.reward, isNewCard) }
        if (previous.todo != content.todo) persist { repository.updateCardTodo(id, content.todo, isNewCard) }
    }

    private fun queueTitleSave(noteId: String, title: String) {
        pendingTitle = title
        titleSaveJob?.cancel()
        titleSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(TEXT_SAVE_DEBOUNCE_MS)
            repository.enqueueTemplateEdit { repository.updateCardTitle(noteId, title, isNewCard) }.join()
            if (pendingTitle == title) pendingTitle = null
        }
    }

    private fun queueDescriptionSave(noteId: String, description: String) {
        pendingDescription = description
        descriptionSaveJob?.cancel()
        descriptionSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(TEXT_SAVE_DEBOUNCE_MS)
            repository.enqueueTemplateEdit { repository.updateCardDescription(noteId, description, isNewCard) }.join()
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
                if (title != null) repository.updateCardTitle(id, title, isNewCard)
                if (description != null) repository.updateCardDescription(id, description, isNewCard)
            }
        }
    }

    suspend fun flushAndAwaitCardEdits() {
        flushPendingCardEdits()
        templateWriteJobs.toList().joinAll()
    }

    fun getSchedule() : Schedule {
        return repository.getSchedule()
    }

    fun getSublistChane() : SublistChain {
        return repository.getSublistChane()
    }

    fun setSchedule(schedule: Schedule) {
        viewModelScope.launch {
            setScheduleAndAwait(schedule)
        }
    }

    suspend fun setScheduleAndAwait(schedule: Schedule) {
        flushAndAwaitCardEdits()
        withContext(Dispatchers.IO) {
            repository.setSchedule(schedule = schedule)
            repository.updateNote()
        }
    }

    private fun updateCardLabels(listName : String) {
        updateCard(_cardContent.value.copy(
            scheduleLabel = repository.getScheduleDescription(),
            listLabel = listName,
            tagsLabel = if (cardContent.value.tags.isEmpty()) repository.getNoTagsText()
                else cardContent.value.tags.fastJoinToString(separator = " #", prefix = "#")
        ))
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

    override fun setDone() {
        viewModelScope.launch (Dispatchers.Default) {
            repository.setCardDone(_cardContent.value)
        }
    }

    override fun showReward(): Boolean {
        return true
    }

    override fun getCardContent(): MutableState<CardContent> {
        return cardContent
    }

    fun inserAndAddTag(tagName: String) {
        viewModelScope.launch (Dispatchers.Default) {
            cardContent.value.id?.let { id ->
                val tagId = repository.insertTag(tagName)
                addTag(NoteTag(tagId, tagName))
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
