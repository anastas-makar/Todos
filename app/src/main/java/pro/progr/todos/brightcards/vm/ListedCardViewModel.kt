package pro.progr.todos.brightcards.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.db.NoteWithData

class ListedCardViewModel(
    val rowKey: CardRowKey,
    cardContent: CardContent,
    note: NoteWithData
) : ViewModel() {
    val card = mutableStateOf(cardContent)
    var note: NoteWithData = note
        private set

    fun updateRowData(
        rowKey: CardRowKey,
        cardContent: CardContent,
        note: NoteWithData
    ) {
        require(this.rowKey == rowKey) { "Cannot change a ListedCardViewModel row identity" }
        card.value = cardContent
        this.note = note
    }
}
