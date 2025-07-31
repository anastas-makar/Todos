package pro.progr.todos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRepository
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.db.ArrayPOJO
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.NoteWithDataDao
import pro.progr.todos.db.SublistChain
import pro.progr.todos.db.TagsDao
import java.time.LocalDate
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val noteAndHistoryDao: NoteAndHistoryDao,
    private val notesWithDataDao: NoteWithDataDao,
    private val tagsDao: TagsDao
) : CardRepository {

    lateinit var note: NoteWithData

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)

    private var scheduleSetForId : String? = null

    override suspend fun getCard(id: String): CardContent {
        observeNoteFromBase(id)

        note = notesWithDataDao.getNote(id)

        //todo
        return NoteConverter.toCardContent(note)
    }

    fun getCardInHistory(id: String, epochDay: Long): CardContent? {
        val noteInHistory = noteAndHistoryDao.getNoteInHistory(id, epochDay)

        noteInHistory?.let {

            val cardContent = NoteInHistoryConverter.toCardContent(noteInHistory)

            note = NoteWithData(NoteConverter.toNote(cardContent), emptyList())

            return cardContent

        }

        return null
    }

    fun updateCardInHistory(title: String, description: String, noteId: Long, epochDay: Long) {
        noteAndHistoryDao.editNoteInHistory(
            title = title,
            description = description,
            noteId = noteId,
            epochDay = epochDay
            )
    }

    fun observeNoteFromBase(id : String) {
        coroutineScope.launch {
            notesWithDataDao.getNoteFlow(id).collectLatest { noteFromBase ->
                noteFromBase?.let {
                    if (this@CardRepository::note.isInitialized && note.note.id == noteFromBase.note.id) {
                        note = noteFromBase

                    }

                }
            }
        }
    }

    override suspend fun saveCard(cardContent: CardContent) : CardContent {
        val note = NoteConverter.toNote(cardContent)
        notesWithDataDao.insert(note)
        return getCard(note.id)
    }

    override suspend fun updateCard(cardContent: CardContent) {

        if (NoteConverter.noteEqualsCard(note.note, cardContent)) {
            return
        }

        //а иначе schedule перезатрётся
        note.note.title = cardContent.title
        note.note.description = cardContent.text
        note.note.style = cardContent.style
        note.note.fillTextBackground = cardContent.fillTextBackground
        note.note.fillTitleBackground = cardContent.fillTitleBackground
        note.note.todo = cardContent.todo
        note.note.reward = cardContent.reward
        note.note.latestDone = cardContent.latestDone

        if (note.note.schedule.pattern.type.datesFilter.isActual(
                note.note.schedule,
                LocalDate.now()
            )) {
            val historyNote = NoteConverter.toNoteInHistory(note = note.note, LocalDate.now().toEpochDay())
            noteAndHistoryDao.updateDaysNoteHistoryIfNotEdited(historyNote, historyNote.date)
        }

        notesWithDataDao.update(note)
    }

    suspend fun updateList(sublistChain : SublistChain) {
        note.note.sublistChain = sublistChain

        if (note.note.schedule.pattern.type.datesFilter.isActual(
                note.note.schedule,
                LocalDate.now()
            )) {
            val historyNote = NoteConverter.toNoteInHistory(note = note.note, LocalDate.now().toEpochDay())
            noteAndHistoryDao.updateDaysNoteHistoryIfNotEdited(historyNote, historyNote.date)
        }

        //notesWithDataDao.update(note)
    }

    override suspend fun deleteCard(cardContent: CardContent): Boolean {
        return notesWithDataDao.deleteWithHistoryForDate(
            NoteConverter.toNote(cardContent),
            LocalDate.now().toEpochDay()
        ) > 0
    }

    fun getSublistChane() : SublistChain {
        return note.note.sublistChain
    }

    fun setSchedule(schedule: Schedule) {
        if (scheduleSetForId != note.note.id) {
            note.note.dateTill = schedule.dates?.till
            note.note.dateSince = schedule.dates?.since
            note.note.patternType = schedule.pattern.type
            note.note.patterDates = schedule.pattern.days
            note.note.cancelledDates = ArrayPOJO(schedule.dates?.cancelledDates)
            note.note.addedDates = ArrayPOJO(schedule.dates?.addedDates)
            scheduleSetForId = note.note.id
        }

        notesWithDataDao.update(note)
    }

    fun getSchedule() : Schedule {
        return note.note.schedule
    }

    override suspend fun setCardDone(cardContent: CardContent) {
        val note = NoteConverter.toNote(cardContent, note.note.schedule)

        note.latestDone = LocalDate.now().toEpochDay()

        val historyNote = NoteInHistory(
            noteId = note.id,
            date = LocalDate.now().toEpochDay(),
            title = note.title,
            description = note.description,
            sublistChain = note.sublistChain,
            schedule = note.schedule,
            style = note.style,
            reward = note.reward,
            fillTitleBackground = note.fillTitleBackground,
            fillTextBackground = note.fillTextBackground,
            todo = TodoStatus.DONE
        )

        noteAndHistoryDao.setCardDoneAndUpdateHistory(note, historyNote, LocalDate.now().toEpochDay())
    }

    fun insertTag(tagName: String) : String {
        val nTag = NoteTag(title = tagName)
        tagsDao.insert(nTag)

        return nTag.id
    }

    fun addTag(tagId: String, noteId: String) {
        notesWithDataDao.addNoteTag(tagId = tagId, noteId = noteId)
    }

    fun removeNoteTag(tagId: String, noteId: String) {
        notesWithDataDao.removeNoteTag(tagId = tagId, noteId = noteId)
    }

    suspend fun updateNote() {
        noteAndHistoryDao.updateNoteDates(note.note)
    }

    suspend fun removeForDay(date: LocalDate, noteId: String) {
        noteAndHistoryDao.remoCardForDay(date.toEpochDay(), noteId)
    }
}