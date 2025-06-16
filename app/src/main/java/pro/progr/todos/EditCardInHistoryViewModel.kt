package pro.progr.todos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pro.progr.brightcards.colors.ColorStyle
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.model.StringAnnotation
import pro.progr.brightcards.vm.CardViewModel
import java.time.LocalDate

class EditCardInHistoryViewModel private constructor(
    private val repository: CardRepository,
    private val _cardContent: MutableStateFlow<CardContent>,
    private val epochDay: Long
) : ViewModel(), CardViewModel {

    internal val cardContent: MutableState<CardContent> = mutableStateOf(_cardContent.value)

    override fun getCardId() : String? {
        return cardContent.value.id
    }

    // Функция для вызова при клике на аннотацию
    override fun onAnnotationClicked(annotation: StringAnnotation) {

    }

    override fun onAnnotationClickFired() {

    }

    // For creating a new card
    constructor(repository: CardRepository, style: ColorStyle, epochDay: Long) : this(
        repository,
        MutableStateFlow(CardContent(style = style)), // create a new CardContent
        epochDay
    ) {
        saveCard(_cardContent.value).invokeOnCompletion {
            observeCard() // Start observing cardContent after it's loaded
        }
    }

    // For editing an existing card
    constructor(repository: CardRepository, id: String, epochDay: Long) : this(
        repository,
        MutableStateFlow(CardContent()), // Temporary empty CardContent until the real one is loaded
        epochDay
    ) {
        getCard(id).invokeOnCompletion {
            observeCard() // Start observing cardContent after it's loaded
        }
    }

    override fun getCard(id: String) : Job {
        return viewModelScope.launch (Dispatchers.Default) {
            repository.getCardInHistory(id.toLong(), epochDay)?.let { cardContent ->
                _cardContent.value = cardContent

            }
        }
    }

    override fun deleteCard() {
    }

    private fun saveCard(content: CardContent): Job {
        TODO()
    }

    override fun updateCard(content: CardContent) {
        _cardContent.value = content
    }

    private fun updateCard() {
        cardContent.value = _cardContent.value

        _cardContent.value.id?.let { cardId ->
            viewModelScope.launch(Dispatchers.Default) {
                repository.updateCardInHistory(title = _cardContent.value.title,
                    description = _cardContent.value.text,
                    noteId = cardId.toLong(),
                    epochDay = epochDay)
            }

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
    }

    override fun showReward(): Boolean {
        return false
    }

    override fun getCardContent(): MutableState<CardContent> {
        return cardContent
    }

    fun removeForDay(date: LocalDate) {
        cardContent.value.id?.let {noteId ->
            viewModelScope.launch(Dispatchers.Default) {

                repository.removeForDay(date, noteId = noteId.toLong())
            }
        }
    }
}
