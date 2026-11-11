package pro.progr.todos

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRepository
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.db.NoteAndHistoryDao
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.NoteWithDataDao
import pro.progr.todos.db.SublistChain
import pro.progr.todos.db.applySchedule
import pro.progr.todos.db.toSchedule
import pro.progr.todos.db.TagsDao
import java.time.LocalDate
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val context: Context,
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
        return NoteConverter.toCardContent(note, context)
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

    fun updateCardInHistory(title: String, description: String, noteId: String, epochDay: Long) {
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

        if ((note.note.patternType ?: pro.progr.todos.datefilters.FilterType.NO_TERMS).datesFilter.isActual(
                note.note.toSchedule(),
                LocalDate.now()
            )) {
            val historyNote = NoteConverter.toNoteInHistory(note = note.note, LocalDate.now().toEpochDay())
            noteAndHistoryDao.updateDaysNoteHistoryIfNotEdited(historyNote, historyNote.date)
        }

        notesWithDataDao.update(note)
    }

    suspend fun updateList(sublistChain : SublistChain) {
        note.note.sublistChain = sublistChain

        if ((note.note.patternType ?: pro.progr.todos.datefilters.FilterType.NO_TERMS).datesFilter.isActual(
                note.note.toSchedule(),
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
            note.note.applySchedule(schedule)
            scheduleSetForId = note.note.id
        }

        notesWithDataDao.update(note)
    }

    fun getSchedule() : Schedule {
        return note.note.toSchedule()
    }

    fun getScheduleDescription(): String =
        getSchedule().pattern.type.datesFilter.getDescription(getSchedule(), context)

    fun getNoTagsText(): String = context.getString(R.string.no_tags)

    override suspend fun setCardDone(cardContent: CardContent) {
        val note = NoteConverter.toNote(cardContent, note.note.toSchedule())

        val currentDate = LocalDate.now().toEpochDay()

        note.latestDone = currentDate

        val historyNote = NoteInHistory(
            id = "${note.id}_$currentDate",
            noteId = note.id,
            date = currentDate,
            title = note.title,
            description = note.description,
            sublistChain = note.sublistChain,
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
