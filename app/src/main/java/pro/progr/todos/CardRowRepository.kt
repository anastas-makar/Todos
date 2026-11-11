package pro.progr.todos

import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.applySchedule
import pro.progr.todos.db.toSchedule
import java.time.LocalDate

class CardRowRepository(private val noteAndHistoryDao: NoteAndHistoryDao, private val note: NoteWithData) {

    suspend fun setCardDone(cardContent: CardContent) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.toSchedule())
        convertedNote.sublistChain = note.note.sublistChain

        val currentDate = LocalDate.now().toEpochDay()

        convertedNote.latestDone = currentDate

        val historyNote = NoteInHistory(
            id = "${convertedNote.id}_$currentDate",
            noteId = convertedNote.id,
            date = currentDate,
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = TodoStatus.DONE
        )

        noteAndHistoryDao.setCardDoneAndUpdateHistory(convertedNote, historyNote, currentDate)
    }

    suspend fun addCardToHistory(cardContent: CardContent, date: LocalDate) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.toSchedule())
        convertedNote.sublistChain = note.note.sublistChain

        val historyNote = NoteInHistory(
            id = "${convertedNote.id}_${date.toEpochDay()}",
            noteId = convertedNote.id,
            date = date.toEpochDay(),
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = convertedNote.todo
        )

        noteAndHistoryDao.insert(historyNote)
    }

    suspend fun addCardForDay(cardContent: CardContent, date: LocalDate) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.toSchedule())
        convertedNote.sublistChain = note.note.sublistChain

        convertedNote.applySchedule(Schedule(
            Schedule.Pattern(
            days = LinkedHashMap(),
            type = FilterType.DATE
        ),
            dates = Schedule.Dates(addedDates = arrayOf(date.toEpochDay()))))

        noteAndHistoryDao.update(convertedNote)
    }

    suspend fun setNoteInHistoryDone(cardContent: CardContent, date: LocalDate) {
        noteAndHistoryDao.setCardDone(NoteConverter.toNote(cardContent, note.note.toSchedule()), date.toEpochDay())
    }

    suspend fun setCardNotDone(cardContent: CardContent) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.toSchedule())
        convertedNote.sublistChain = note.note.sublistChain

        convertedNote.todo = TodoStatus.TODO

        val currentDate = LocalDate.now().toEpochDay()

        val historyNote = NoteInHistory(
            id = "${convertedNote.id}_$currentDate",
            noteId = convertedNote.id,
            date = currentDate,
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = TodoStatus.TODO
        )

        noteAndHistoryDao.setCardNotDoneAndUpdateHistory(convertedNote, historyNote, LocalDate.now().toEpochDay())
    }
    suspend fun removeForDay(date: LocalDate, noteId: String) {
        noteAndHistoryDao.remoCardForDay(date.toEpochDay(), noteId)
    }
}
