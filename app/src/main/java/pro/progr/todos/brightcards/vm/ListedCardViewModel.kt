package pro.progr.todos.brightcards.vm

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.brightcards.model.ListedCardRepositoryInterface
import pro.progr.todos.brightcards.model.TodoStatus
import java.time.LocalDate

class ListedCardViewModel(
    val rowKey: CardRowKey,
    cardContent: CardContent,
    repository: ListedCardRepositoryInterface,
    private val context: Context
) : ViewModel() {
    val card = mutableStateOf(cardContent)
    var repository: ListedCardRepositoryInterface = repository
        private set

    val menuOn = mutableStateOf(false)

    val destination = mutableStateOf(Destinations.NONE)

    fun updateRowData(
        rowKey: CardRowKey,
        cardContent: CardContent,
        repository: ListedCardRepositoryInterface
    ) {
        require(this.rowKey == rowKey) { "Cannot change a ListedCardViewModel row identity" }
        card.value = cardContent
        this.repository = repository
    }

    fun setDone() {
        card.value = card.value.copy(todo = TodoStatus.DONE)

        viewModelScope.launch (Dispatchers.Default) {
            repository.setCardDone(card.value)
        }
    }

    fun setNoteInHistoryDone(date: LocalDate) {
        card.value = card.value.copy(todo = TodoStatus.DONE)

        viewModelScope.launch (Dispatchers.Default) {
            repository.setNoteInHistoryDone(card.value, date)
        }
    }

    fun setNotDone() {
        card.value = card.value.copy(todo = TodoStatus.TODO)

        viewModelScope.launch (Dispatchers.Default) {
            repository.setCardNotDone(card.value)
        }
    }

    fun setMenu(date: LocalDate) {
        card.value.menuItems = CardMenuItems(context, date, card.value.todo, {updDestination -> this.destination.value = updDestination})
    }

    fun setMenu() {
        card.value.menuItems = UndatedCardMenuItems(context, card.value.todo, {updDestination -> this.destination.value = updDestination})
    }

    fun removeForDay(date: LocalDate) {
        card.value.id?.let {noteId ->
            viewModelScope.launch(Dispatchers.Default) {

                repository.removeForDay(date, noteId = noteId)
            }
        }
    }

    fun doToday() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.addCardForDay(cardContent = card.value, date = LocalDate.now())
            repository.addCardToHistory(cardContent = card.value, date = LocalDate.now())
        }
    }

    fun doTomorrow() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.addCardForDay(cardContent = card.value,
                date = LocalDate.now().plusDays(1L))
        }
    }

}
