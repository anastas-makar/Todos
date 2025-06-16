package pro.progr.todos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pro.progr.brightcards.colors.ColorStyle
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.model.CardTag
import pro.progr.brightcards.model.StringAnnotation
import pro.progr.brightcards.vm.CardViewModel
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.SublistChain

class CardViewModel private constructor(
    private val repository: CardRepository,
    private val _cardContent: MutableStateFlow<CardContent>
) : ViewModel(), CardViewModel {

    internal val cardContent: MutableState<CardContent> = mutableStateOf(_cardContent.value)

    // MutableStateFlow для хранения актуальной аннотации
    private val _annotationClicked = MutableStateFlow<StringAnnotation?>(null)
    val annotationClicked: Flow<StringAnnotation?> = _annotationClicked

    override fun getCardId() : String? {
        return cardContent.value.id
    }

    // Функция для вызова при клике на аннотацию
    override fun onAnnotationClicked(annotation: StringAnnotation) {
        _annotationClicked.value = annotation
    }

    override fun onAnnotationClickFired() {
        _annotationClicked.value = null
    }

    constructor(repository: CardRepository, style: ColorStyle) : this(
        repository,
        MutableStateFlow(CardContent(style = style))
    ) {
        saveCard(_cardContent.value).invokeOnCompletion {
            observeCard()
        }
    }

    constructor(repository: CardRepository, id: String) : this(
        repository,
        MutableStateFlow(CardContent())
    ) {
        getCard(id).invokeOnCompletion {
            observeCard()
        }
    }

    override fun getCard(id: String) : Job {
        return viewModelScope.launch (Dispatchers.Default) {
            _cardContent.value = repository.getCard(id)
        }
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
            }
        }
    }

    override fun updateCard(content: CardContent) {
        _cardContent.value = content
    }

    fun getSchedule() : Schedule {
        return repository.getSchedule()
    }

    fun getSublistChane() : SublistChain {
        return repository.getSublistChane()
    }

    fun setSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.setSchedule(schedule = schedule)
            repository.updateNote()
        }
    }

    private fun updateCard() {
        cardContent.value = _cardContent.value

        viewModelScope.launch(Dispatchers.Default) {
            repository.updateCard(_cardContent.value)
        }
    }

    private fun updateStringAnnotations(listName : String) {
        updateCard(_cardContent.value.copy(
            stringAnnotations = listOf(
                StringAnnotation(
                    "schedule",
                    getSchedule().pattern.type.datesFilter.getDescription(
                        getSchedule()
                    )
                ),
                StringAnnotation(
                    "list",
                    listName
                ),
                StringAnnotation(
                    "tags",
                    if (cardContent.value.tags.isEmpty()) "Нет тегов" else cardContent.value.tags.fastJoinToString(separator = " #", prefix = "#")
                )
            )
        ))
    }

    fun updateList(list : NList) {
        updateStringAnnotations(list.getName())

        viewModelScope.launch (Dispatchers.Default) {
            repository.updateList(list.sublistChain)
        }
    }

    private fun observeCard() {
        _cardContent.onEach { content ->
            if (content.id != null) {
                updateCard()
            }
        }.launchIn(viewModelScope)
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
                repository.addTag(tagId = tag.id, noteId = id.toLong())
                _cardContent.value =
                    _cardContent.value.copy(
                        tags = _cardContent.value.tags.plus(CardTag(tag.id, tag.title)))
            }
        }
    }

    fun removeNoteTag(tag: NoteTag) {
        viewModelScope.launch (Dispatchers.Default) {
            cardContent.value.id?.let { id ->
                repository.removeNoteTag(tagId = tag.id, noteId = id.toLong())
                _cardContent.value = _cardContent.value.copy(
                    tags = _cardContent.value.tags.filter {
                            cardTag -> cardTag.id != tag.id })
            }
        }
    }
}
