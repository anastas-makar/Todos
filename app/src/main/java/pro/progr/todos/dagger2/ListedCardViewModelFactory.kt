package pro.progr.todos.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.ListedCardRepository
import pro.progr.todos.db.Note
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteWithData
import javax.inject.Inject

class ListedCardViewModelFactory @Inject constructor(private val noteAndHistoryDao: NoteAndHistoryDao) : ViewModelProvider.Factory {

    lateinit var cardContent: CardContent
    lateinit var note: NoteWithData

    fun createWithCardContent(cardContent: CardContent): ListedCardViewModelFactory {
        this.cardContent = cardContent
        return this
    }

    fun createWithNote(note: NoteWithData): ListedCardViewModelFactory {
        this.note = note
        return this
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListedCardViewModel::class.java)) {
            return ListedCardViewModel(cardContent = cardContent, repository = ListedCardRepository(noteAndHistoryDao, note)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}