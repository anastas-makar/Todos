package pro.progr.todos.dagger2

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.todos.CardRow
import pro.progr.todos.ListedCardRepository
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteWithData
import javax.inject.Inject

class ListedCardViewModelFactory @Inject constructor(private val noteAndHistoryDao: NoteAndHistoryDao, private val context: Context) : ViewModelProvider.Factory {

    lateinit var cardContent: CardContent
    lateinit var note: NoteWithData
    lateinit var rowKey: CardRowKey

    fun createForRow(row: CardRow): ListedCardViewModelFactory {
        this.rowKey = row.key
        this.cardContent = row.content
        this.note = row.note
        return this
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListedCardViewModel::class.java)) {
            return ListedCardViewModel(
                rowKey = rowKey,
                cardContent = cardContent,
                repository = ListedCardRepository(noteAndHistoryDao, note),
                context = context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    fun updateExisting(
        viewModel: ListedCardViewModel,
        row: CardRow
    ): ListedCardViewModel {
        viewModel.updateRowData(row.key, row.content, ListedCardRepository(noteAndHistoryDao, row.note))
        return viewModel
    }
}
