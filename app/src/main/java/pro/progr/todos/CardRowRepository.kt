package pro.progr.todos

import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.db.ArrayPOJO
import pro.progr.todos.db.NoteAndDateEntryDao
import pro.progr.todos.NoteDateEntryConverter
import pro.progr.todos.db.NoteWithData
import java.time.LocalDate

class CardRowRepository(
    private val noteAndDateEntryDao: NoteAndDateEntryDao,
    private val note: NoteWithData
) {
    suspend fun saveForDate(content: CardContent, date: LocalDate, deleted: Boolean = false) {
        noteAndDateEntryDao.upsertDateEntry(
            NoteDateEntryConverter.fromCardContent(content, date.toEpochDay(), deleted)
        )
    }

    suspend fun scheduleUnscheduledNoteForDate(date: LocalDate): Boolean {
        val epochDay = date.toEpochDay()
        return noteAndDateEntryDao.scheduleUnscheduledNoteForDate(
            noteId = note.note.id,
            addedDates = ArrayPOJO(arrayOf(epochDay)),
            cancelledDates = ArrayPOJO(emptyArray<Long>()),
            patternDays = linkedMapOf(),
            epochDay = epochDay
        ) > 0
    }

    suspend fun setCardDone(content: CardContent, date: LocalDate) {
        if (note.note.patternType == pro.progr.todos.datefilters.FilterType.NO_TERMS) {
            noteAndDateEntryDao.updateUnscheduledNoteDone(note.note.id)
        }
        saveForDate(content.copy(todo = TodoStatus.DONE), date)
    }

    suspend fun setCardNotDone(content: CardContent, date: LocalDate) {
        if ((note.note.patternType ?: pro.progr.todos.datefilters.FilterType.NO_TERMS) == pro.progr.todos.datefilters.FilterType.NO_TERMS) {
            noteAndDateEntryDao.updateUnscheduledNoteNotDone(note.note.id)
        }
        saveForDate(content.copy(todo = TodoStatus.TODO), date)
    }

    suspend fun removeForDay(content: CardContent, date: LocalDate) {
        saveForDate(content, date, deleted = true)
    }
}
