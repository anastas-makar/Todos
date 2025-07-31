package pro.progr.todos

import kotlinx.coroutines.flow.Flow
import pro.progr.todos.db.Note
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NotesDao
import pro.progr.flow.model.DatesRepository
import java.time.LocalDate
import javax.inject.Inject

class DatesGridRepository @Inject constructor(private val notesDao: NotesDao,
                                              private val notesAndHistoryDao: NoteAndHistoryDao) : DatesRepository {

    val startDate = LocalDate.now()

    //private val _selectedDates = mutableStateOf(listOf(LocalDate.now()))
    val selectedDates : HashSet<LocalDate> = hashSetOf()

    fun getNote(id : String) : Flow<Note?> {
        return notesDao.getNoteFlow(id)
    }

    fun setNoteDates(sDates : HashSet<LocalDate>) {
        selectedDates.clear()
        selectedDates.addAll(sDates)
    }

    override fun getAnchorDate(): LocalDate {
        return if (selectedDates.isEmpty()) startDate else selectedDates.first()
    }

    override fun isDateSelected(date: LocalDate): Boolean {
        return selectedDates.contains(date)
    }

    override fun updateDate(date: LocalDate) {
        if (selectedDates.contains(date) ) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
    }

    suspend fun updateNote(note: Note) {
        notesAndHistoryDao.updateNoteDates(note)
    }
}