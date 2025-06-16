package pro.progr.todos.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.todos.DatesGridRepository
import pro.progr.todos.NoteCalendarViewModel
import javax.inject.Inject

class NoteCalendarViewModelFactory @Inject constructor(
    private val repository: DatesGridRepository
) : ViewModelProvider.Factory {
    var noteId: Long = 0

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteCalendarViewModel::class.java)) {
            return NoteCalendarViewModel(repository, noteId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}