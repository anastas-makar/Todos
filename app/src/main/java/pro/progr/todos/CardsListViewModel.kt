package pro.progr.todos

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.CardMenuItems
import pro.progr.todos.brightcards.vm.Destinations
import pro.progr.todos.brightcards.vm.UndatedCardMenuItems
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteAndDateEntryDao
import pro.progr.todos.NoteDateEntryConverter
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.NotesRepository
import pro.progr.todos.db.SublistChain
import pro.progr.todos.db.toSchedule
import java.time.LocalDate
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CardsListViewModel @Inject constructor(
    private val context: Context,
    private val notesRepository: NotesRepository,
    private val noteAndDateEntryDao: NoteAndDateEntryDao
) : ViewModel() {

    val cardsList: MutableState<List<CardRow>> = mutableStateOf(emptyList())

    val selectedTag: MutableState<NoteTag?> = mutableStateOf(null)

    val notifications: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    val cardsForDates: HashMap<LocalDate, MutableStateFlow<List<CardRow>>> = HashMap()

    init {
        observeCards()
    }

    private fun observeCards() {

        viewModelScope.launch {

            notesRepository.notesListFlow.collectLatest { notesList ->

                cardsList.value = notesList.map { note ->

                    val cardContent = NoteConverter.toCardContent(note, context)

                    if (note.note.latestDone == LocalDate.now().toEpochDay()) {
                        cardContent.todo = TodoStatus.DONE
                    }

                    CardRow(CardRowKey(note.note.id, null), note, mutableStateOf(cardContent))
                }
            }
        }

    }

    fun getCardsForDate(date: LocalDate): MutableStateFlow<List<CardRow>> {
        if (!cardsForDates.containsKey(date)) {
            val listsFlow = MutableStateFlow(emptyList<CardRow>())

            cardsForDates.put(
                date,
                listsFlow
            )

            viewModelScope.launch {
                combine(notesRepository.notesListFlow, notesRepository.dateEntriesFlow) { notes, entries ->
                    val day = date.toEpochDay()
                    val entriesForDate = entries.filter { it.dateEntry.date == day }
                    val byId = notes.associateBy { it.note.id }
                    val relevantEntries = entriesForDate.filter { notesRepository.matchesDateEntryQuery(it) }
                    val supersededNoteIds = entriesForDate.map { it.dateEntry.noteId }.toSet()

                    val snapshotRows = relevantEntries.filterNot { it.dateEntry.deleted }.map { entry ->
                        val owner = byId[entry.dateEntry.noteId] ?: NoteWithData(entry.note, entry.tags)
                        val content = NoteDateEntryConverter.toCardContent(entry, context)
                        content.todo = statusForDate(content.todo, date)
                        CardRow(CardRowKey(entry.dateEntry.noteId, day), owner, mutableStateOf(content))
                    }

                    val templateRows = notes.asSequence()
                        .filter { it.note.id !in supersededNoteIds }
                        .filter { note ->
                            (note.note.patternType ?: FilterType.NO_TERMS).datesFilter.isActual(
                                note.note.toSchedule(), date
                            )
                        }
                        .map { note ->
                            val content = NoteConverter.toCardContent(note, context)
                            content.todo = if (note.note.todo == TodoStatus.NOT_ACTIVE) {
                                TodoStatus.NOT_ACTIVE
                            } else {
                                when {
                                    date.isBefore(LocalDate.now()) -> TodoStatus.FAIL
                                    date.isAfter(LocalDate.now()) -> TodoStatus.TODO_NOT_ACTIVE
                                    else -> TodoStatus.TODO
                                }
                            }
                            CardRow(CardRowKey(note.note.id, day), note, mutableStateOf(content))
                        }
                        .toList()
                    snapshotRows + templateRows
                }.collectLatest { listsFlow.value = it }

            }

        }

        return cardsForDates[date]!!

    }

    private fun statusForDate(status: TodoStatus, date: LocalDate): TodoStatus = when {
        date.isAfter(LocalDate.now()) && status in setOf(TodoStatus.TODO, TodoStatus.DONE, TodoStatus.TODO_NOT_ACTIVE) -> TodoStatus.TODO_NOT_ACTIVE
        status == TodoStatus.DONE -> TodoStatus.DONE
        status == TodoStatus.TODO && date.isBefore(LocalDate.now()) -> TodoStatus.FAIL
        status == TodoStatus.TODO && date.isAfter(LocalDate.now()) -> TodoStatus.TODO_NOT_ACTIVE
        else -> status
    }

    fun searchNotes(searchString: String) {
        notesRepository.updateQuery(notesRepository.getQueryBuilder().withDescription(searchString))
    }

    fun updateListQuery(showAllTemplates: Boolean, searchString: String) {
        notesRepository.updateQuery(
            notesRepository.getQueryBuilder()
                .withDescription(searchString)
                .withFilterType(if (showAllTemplates) null else FilterType.NO_TERMS)
                .withNotDone(!showAllTemplates)
        )
    }

    fun updateTag(noteTag: NoteTag) {
        selectedTag.value = noteTag
        notesRepository.updateQuery(notesRepository.getQueryBuilder().withTags(hashSetOf(noteTag)))
    }

    fun clearTags() {
        selectedTag.value = null
        notesRepository.updateQuery(notesRepository.getQueryBuilder().withTags(hashSetOf()))
    }

    fun menuItems(
        card: CardRow,
        date: LocalDate?,
        onAction: (Destinations) -> Unit
    ): List<MenuItem> = if (date == null) {
        UndatedCardMenuItems(context, card.content.value.todo, onAction)
    } else {
        CardMenuItems(context, date, card.content.value.todo, onAction)
    }

    fun handleCardAction(
        card: CardRow,
        action: Destinations,
        date: LocalDate,
        navigate: (String) -> Unit,
        updateReward: (Int, LocalDate) -> Unit
    ) {
        if (date.isAfter(LocalDate.now()) &&
            (action == Destinations.MARK_DONE || action == Destinations.MARK_DATE_ENTRY_DONE)
        ) return

        val content = card.content.value
        val repository = CardRowRepository(noteAndDateEntryDao, card.note)

        when (action) {
            Destinations.DO_TODAY -> viewModelScope.launch(Dispatchers.IO) {
                val today = LocalDate.now()
                repository.saveForDate(content, today)
            }
            Destinations.DO_TOMORROW -> viewModelScope.launch(Dispatchers.IO) {
                repository.saveForDate(content, LocalDate.now().plusDays(1))
            }
            Destinations.MARK_DONE -> {
                val doneContent = content.copy(todo = TodoStatus.DONE)
                card.content.value = doneContent
                viewModelScope.launch(Dispatchers.IO) { repository.setCardDone(doneContent, date) }
                updateReward(content.reward, date)
            }
            Destinations.MARK_NOT_DONE -> {
                val notDoneContent = content.copy(todo = TodoStatus.TODO)
                card.content.value = notDoneContent
                viewModelScope.launch(Dispatchers.IO) { repository.setCardNotDone(notDoneContent, date) }
                updateReward(-content.reward, date)
            }
            Destinations.MARK_DATE_ENTRY_DONE -> {
                val doneContent = content.copy(todo = TodoStatus.DONE)
                card.content.value = doneContent
                viewModelScope.launch(Dispatchers.IO) {
                    repository.setCardDone(doneContent, date)
                }
                updateReward(content.reward, date)
            }
            Destinations.REMOVE_FOR_DAY -> content.id?.let { noteId ->
                viewModelScope.launch(Dispatchers.IO) { repository.removeForDay(content, date) }
            }
            Destinations.TO_EDIT_CARD -> content.id?.let { navigate("editCard/$it") }
            Destinations.TO_EDIT_NOTE_FOR_DATE -> content.id?.let { noteId ->
                navigate("editNoteDateEntry/$noteId/date/${date.toEpochDay()}")
            }
            Destinations.NONE -> Unit
        }
    }

    fun useList(sublistChain: SublistChain) {
        notesRepository.updateQuery(
            notesRepository.getQueryBuilder().withSublistChain(sublistChain)
        )
    }

    //это поиск для календаря
    fun flushNotesSearch() {
        if (!notesRepository.getQueryBuilder().isEmpty()) {
            notesRepository.updateQuery(notesRepository.getQueryBuilder()
                .withNotDone(false)
                .withTitle("")
                .withDescription("")
                .withFilterType(null)
                .withOrder(""))
        }
    }

    //Это поиск для списка
    fun flushNotesList() {
        notesRepository.updateQuery(
            notesRepository.getQueryBuilder()
                .withFilterType(FilterType.NO_TERMS)
                .withNotDone(true)
        )
    }

    fun deleteEmptyNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            val deletedCount = notesRepository.deleteEmptyNotes()
            if (deletedCount > 0) {
                notifications.value =
                    notifications.value + listOf(context.getString(R.string.delete_empty_notes, deletedCount))
            }
        }
    }

}
