package pro.progr.todos

import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.ListedCardRepositoryInterface
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.NoteWithData
import java.time.LocalDate

class ListedCardRepository(private val noteAndHistoryDao: NoteAndHistoryDao, private val note: NoteWithData) : ListedCardRepositoryInterface {

    override suspend fun setCardDone(cardContent: CardContent) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.schedule)
        convertedNote.sublistChain = note.note.sublistChain

        convertedNote.latestDone = LocalDate.now().toEpochDay()

        val historyNote = NoteInHistory(
            noteId = convertedNote.id,
            date = LocalDate.now().toEpochDay(),
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            schedule = convertedNote.schedule,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = TodoStatus.DONE
        )

        noteAndHistoryDao.setCardDoneAndUpdateHistory(convertedNote, historyNote, LocalDate.now().toEpochDay())
    }

    override suspend fun addCardToHistory(cardContent: CardContent, date: LocalDate) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.schedule)
        convertedNote.sublistChain = note.note.sublistChain

        val historyNote = NoteInHistory(
            noteId = convertedNote.id,
            date = date.toEpochDay(),
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            schedule = convertedNote.schedule,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = convertedNote.todo
        )

        noteAndHistoryDao.insert(historyNote)
    }

    override suspend fun addCardForDay(cardContent: CardContent, date: LocalDate) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.schedule)
        convertedNote.sublistChain = note.note.sublistChain

        convertedNote.schedule = Schedule(
            Schedule.Pattern(
            days = LinkedHashMap(),
            type = FilterType.DATE
        ),
            dates = Schedule.Dates(addedDates = arrayOf(date.toEpochDay())))

        noteAndHistoryDao.update(convertedNote)
    }

    override suspend fun setNoteInHistoryDone(cardContent: CardContent, date: LocalDate) {
        noteAndHistoryDao.setCardDone(NoteConverter.toNote(cardContent, note.note.schedule), date.toEpochDay())
    }

    override suspend fun setCardNotDone(cardContent: CardContent) {

        val convertedNote = NoteConverter.toNote(cardContent, note.note.schedule)
        convertedNote.sublistChain = note.note.sublistChain

        convertedNote.todo = TodoStatus.TODO

        val historyNote = NoteInHistory(
            noteId = convertedNote.id,
            date = LocalDate.now().toEpochDay(),
            title = convertedNote.title,
            description = convertedNote.description,
            sublistChain = convertedNote.sublistChain,
            schedule = convertedNote.schedule,
            style = convertedNote.style,
            reward = convertedNote.reward,
            fillTitleBackground = convertedNote.fillTitleBackground,
            fillTextBackground = convertedNote.fillTextBackground,
            todo = TodoStatus.TODO
        )

        noteAndHistoryDao.setCardNotDoneAndUpdateHistory(convertedNote, historyNote, LocalDate.now().toEpochDay())
    }
    override suspend fun removeForDay(date: LocalDate, noteId: String) {
        noteAndHistoryDao.remoCardForDay(date.toEpochDay(), noteId)
    }
}