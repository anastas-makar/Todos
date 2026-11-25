package pro.progr.todos

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRepository
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.db.NoteAndDateEntryDao
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.NoteWithDataDao
import pro.progr.todos.db.SublistChain
import pro.progr.todos.db.applySchedule
import pro.progr.todos.db.toSchedule
import pro.progr.todos.datefilters.FilterType
import java.time.LocalDate
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val context: Context,
    private val noteAndDateEntryDao: NoteAndDateEntryDao,
    private val notesWithDataDao: NoteWithDataDao,
    private val tagsRepository: TagsRepository
) : CardRepository {

    lateinit var note: NoteWithData

    private val templateEditMutex = Mutex()
    private val templateEditScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val templateEditQueue = Channel<TemplateEditRequest>(Channel.UNLIMITED)

    init {
        templateEditScope.launch {
            for (request in templateEditQueue) {
                try {
                    request.update()
                    request.completed.complete(Unit)
                } catch (failure: Exception) {
                    request.completed.completeExceptionally(failure)
                }
            }
        }
    }

    override suspend fun getCard(id: String): CardContent {
        note = notesWithDataDao.getNote(id)

        //todo
        return NoteConverter.toCardContent(note, context)
    }

    /** Persist one editor field without replacing unrelated values from a stale snapshot. */
    suspend fun updateCardTitle(noteId: String, title: String) = persistCardField {
        notesWithDataDao.updateTitle(noteId, title)
    }

    suspend fun updateCardDescription(noteId: String, description: String) = persistCardField {
        notesWithDataDao.updateDescription(noteId, description)
    }

    suspend fun updateCardStyle(noteId: String, style: pro.progr.todos.brightcards.colors.ColorStyle) = persistCardField {
        notesWithDataDao.updateStyle(noteId, style)
    }

    suspend fun updateCardTitleBackground(noteId: String, filled: Boolean) = persistCardField {
        notesWithDataDao.updateTitleBackground(noteId, filled)
    }

    suspend fun updateCardDescriptionBackground(noteId: String, filled: Boolean) = persistCardField {
        notesWithDataDao.updateDescriptionBackground(noteId, filled)
    }

    suspend fun updateCardReward(noteId: String, reward: Int) = persistCardField {
        notesWithDataDao.updateReward(noteId, reward)
    }

    suspend fun updateCardTodo(noteId: String, todo: TodoStatus) = persistCardField {
        notesWithDataDao.updateTodo(noteId, todo)
    }

    /** The edit survives navigation clearing the screen ViewModel. */
    fun enqueueTemplateEdit(update: suspend () -> Unit): Job {
        val completed = CompletableDeferred<Unit>()
        val request = TemplateEditRequest(update, completed)
        val result = templateEditQueue.trySend(request)
        if (result.isFailure) {
            completed.completeExceptionally(result.exceptionOrNull() ?: IllegalStateException("template edit queue is closed"))
        }
        return templateEditScope.launch { completed.await() }
    }

    private data class TemplateEditRequest(
        val update: suspend () -> Unit,
        val completed: CompletableDeferred<Unit>
    )

    private suspend fun persistCardField(update: suspend () -> Unit) {
        templateEditMutex.withLock {
            update()
        }
    }

    suspend fun getCardForDate(id: String, epochDay: Long): CardContent {
        val entry = noteAndDateEntryDao.getDateEntryWithData(id, epochDay)
        val content = if (entry != null) {
            NoteDateEntryConverter.toCardContent(entry, context)
        } else {
            NoteConverter.toCardContent(notesWithDataDao.getNote(id), context)
        }
        content.todo = when {
            epochDay > LocalDate.now().toEpochDay() && content.todo in setOf(TodoStatus.TODO, TodoStatus.DONE, TodoStatus.TODO_NOT_ACTIVE) -> TodoStatus.TODO_NOT_ACTIVE
            content.todo == TodoStatus.DONE -> TodoStatus.DONE
            content.todo == TodoStatus.TODO && epochDay < LocalDate.now().toEpochDay() -> TodoStatus.FAIL
            content.todo == TodoStatus.TODO && epochDay > LocalDate.now().toEpochDay() -> TodoStatus.TODO_NOT_ACTIVE
            else -> content.todo
        }
        return content
    }

    suspend fun updateDateEntry(content: CardContent, epochDay: Long) {
        templateEditMutex.withLock {
            noteAndDateEntryDao.upsertDateEntry(
                NoteDateEntryConverter.fromCardContent(content, epochDay)
            )
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

        notesWithDataDao.update(note)
    }

    suspend fun updateList(sublistChain : SublistChain) {
        note.note.sublistChain = sublistChain

        //notesWithDataDao.update(note)
    }

    override suspend fun deleteCard(cardContent: CardContent): Boolean {
        return notesWithDataDao.delete(requireNotNull(cardContent.id)) > 0
    }

    fun getSublistChane() : SublistChain {
        return note.note.sublistChain
    }

    suspend fun updateCardSchedule(schedule: Schedule) {
        val noteId = note.note.id
        val updated = notesWithDataDao.getNote(noteId).note
        updated.applySchedule(schedule)
        noteAndDateEntryDao.updateScheduleColumns(
            noteId, updated.addedDates, updated.cancelledDates, updated.patterDates,
            updated.patternType, updated.dateSince, updated.dateTill
        )
        note.note.applySchedule(schedule)
    }

    fun getSchedule() : Schedule {
        return note.note.toSchedule()
    }

    fun getScheduleDescription(): String =
        getSchedule().pattern.type.datesFilter.getDescription(getSchedule(), context)

    fun getNoTagsText(): String = context.getString(R.string.no_tags)

    override suspend fun setCardDone(cardContent: CardContent) {
        val currentDate = LocalDate.now().toEpochDay()
        updateDateEntry(cardContent.copy(todo = TodoStatus.DONE), currentDate)
        if ((note.note.patternType ?: FilterType.NO_TERMS) == FilterType.NO_TERMS) {
            noteAndDateEntryDao.updateUnscheduledNoteDone(note.note.id)
        }
    }

    suspend fun insertTag(tagName: String): NoteTag? = tagsRepository.findOrCreate(tagName)

    fun addTag(tagId: String, noteId: String) {
        notesWithDataDao.addNoteTag(tagId = tagId, noteId = noteId)
    }

    fun removeNoteTag(tagId: String, noteId: String) {
        notesWithDataDao.removeNoteTag(tagId = tagId, noteId = noteId)
    }

    suspend fun updateNote() {
        noteAndDateEntryDao.update(note.note)
    }

    suspend fun removeForDay(content: CardContent, date: LocalDate) {
        templateEditMutex.withLock {
            noteAndDateEntryDao.upsertDateEntry(
                NoteDateEntryConverter.fromCardContent(content, date.toEpochDay(), deleted = true)
            )
        }
    }
}
