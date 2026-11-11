package pro.progr.todos

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.CardMenuItems
import pro.progr.todos.brightcards.vm.Destinations
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.brightcards.vm.UndatedCardMenuItems
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteAndHistoryDao
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
    private val noteAndHistoryDao: NoteAndHistoryDao
) : ViewModel() {

    val cardsList: MutableState<List<ListedCardViewModel>> = mutableStateOf(emptyList())

    val selectedTag: MutableState<NoteTag?> = mutableStateOf(null)

    val notifications: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    val cardsForDates: HashMap<LocalDate, MutableStateFlow<List<ListedCardViewModel>>> = HashMap()

    private lateinit var viewModelCreator: (CardRow) -> ListedCardViewModel

    fun setViewModelCreator(creator: (CardRow) -> ListedCardViewModel) {
        viewModelCreator = creator
    }

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

                    viewModelCreator(CardRow(CardRowKey(note.note.id, null), note, cardContent))
                }
            }
        }

    }

    fun getCardsForDate(date: LocalDate): MutableStateFlow<List<ListedCardViewModel>> {
        if (!cardsForDates.containsKey(date)) {
            val listsFlow = MutableStateFlow(emptyList<ListedCardViewModel>())

            cardsForDates.put(
                date,
                listsFlow
            )

            viewModelScope.launch {
                notesRepository.notesListFlow.collectLatest { notesList ->
                    listsFlow.value = notesList
                        .filter { note ->
                            (note.note.patternType ?: FilterType.NO_TERMS).datesFilter.isActual(
                                note.note.toSchedule(),
                                date
                            )
                        }.map { note ->
                            val cardContent = NoteConverter.toCardContent(note, context)

                            if (date.isEqual(LocalDate.now())) {
                                //задачи на сегодня
                                if (note.note.latestDone == LocalDate.now().toEpochDay()) {
                                    cardContent.todo = TodoStatus.DONE
                                }
                            } else if (date.isAfter(LocalDate.now())) {
                                //задачи на будущее
                                if (note.note.todo == TodoStatus.TODO) {
                                    cardContent.todo = TodoStatus.TODO_NOT_ACTIVE
                                }
                            }

                            viewModelCreator(
                                CardRow(CardRowKey(note.note.id, date.toEpochDay()), note, cardContent)
                            )
                        }
                }

            }

        }

        return cardsForDates[date]!!

    }

    fun getHistoryFlow() : Flow<Map<Long, List<CardContent>>> {
        return notesRepository.notesInHistoryFlow
    }

    fun getHistoryViewModels(cards: List<CardContent>, date: LocalDate): List<ListedCardViewModel> {

        /**
         * TODO:
         * Тут может быть баг:
         * до полуночи открыть режим "не в календаре",
         * пометить дело сделанным (появится карточка в истории на сегодня),
         * --> и дальше в календаре будет отображаться только эта карточка
         */
        if (cards.isEmpty()) {
            saveTodaysNotes()
        }

        return cards.map { cardContent ->
            if (TodoStatus.TODO.equals(cardContent.todo) && date.isBefore(LocalDate.now())) cardContent.todo = TodoStatus.FAIL

            val note = NoteWithData(
                    note = NoteConverter.toNote(cardContent),
                    tags = cardContent.tags.map { tag -> NoteTag(tag.id, tag.title) }
            )
            viewModelCreator(
                CardRow(
                    CardRowKey(cardContent.id ?: error("History card is missing noteId"), date.toEpochDay()),
                    note,
                    cardContent
                )
            )
        }
    }

    fun searchNotes(searchString: String) {
        notesRepository.updateQuery(notesRepository.getQueryBuilder().withDescription(searchString))
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
        card: ListedCardViewModel,
        date: LocalDate?,
        onAction: (Destinations) -> Unit
    ): List<MenuItem> = if (date == null) {
        UndatedCardMenuItems(context, card.card.value.todo, onAction)
    } else {
        CardMenuItems(context, date, card.card.value.todo, onAction)
    }

    fun handleCardAction(
        card: ListedCardViewModel,
        action: Destinations,
        date: LocalDate,
        navigate: (String) -> Unit,
        updateReward: (Int, LocalDate) -> Unit
    ) {
        val content = card.card.value
        val repository = ListedCardRepository(noteAndHistoryDao, card.note)

        when (action) {
            Destinations.DO_TODAY -> viewModelScope.launch(Dispatchers.IO) {
                val today = LocalDate.now()
                repository.addCardForDay(content, today)
                repository.addCardToHistory(content, today)
            }
            Destinations.DO_TOMORROW -> viewModelScope.launch(Dispatchers.IO) {
                repository.addCardForDay(content, LocalDate.now().plusDays(1))
            }
            Destinations.MARK_DONE -> {
                val doneContent = content.copy(todo = TodoStatus.DONE)
                card.card.value = doneContent
                viewModelScope.launch(Dispatchers.IO) { repository.setCardDone(doneContent) }
                updateReward(content.reward, LocalDate.now())
            }
            Destinations.MARK_NOT_DONE -> {
                val notDoneContent = content.copy(todo = TodoStatus.TODO)
                card.card.value = notDoneContent
                viewModelScope.launch(Dispatchers.IO) { repository.setCardNotDone(notDoneContent) }
                updateReward(-content.reward, LocalDate.now())
            }
            Destinations.MARK_NOTE_IN_HISTORY_DONE -> {
                val doneContent = content.copy(todo = TodoStatus.DONE)
                card.card.value = doneContent
                viewModelScope.launch(Dispatchers.IO) {
                    repository.setNoteInHistoryDone(doneContent, date)
                }
                updateReward(content.reward, date)
            }
            Destinations.REMOVE_FOR_DAY -> content.id?.let { noteId ->
                viewModelScope.launch(Dispatchers.IO) { repository.removeForDay(date, noteId) }
            }
            Destinations.TO_EDIT_CARD -> content.id?.let { navigate("editCard/$it") }
            Destinations.TO_EDIT_NOTE_IN_HISTORY -> content.id?.let { noteId ->
                navigate("editNoteInHistory/$noteId/date/${date.toEpochDay()}")
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
            val deletedCount = notesRepository.deleteUnlinkedHistoryForToday(LocalDate.now().toEpochDay())
            if (deletedCount > 0) {
                notifications.value =
                    notifications.value + listOf(context.getString(R.string.delete_empty_notes, deletedCount))
            }
        }
    }

    fun saveTodaysNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            notesRepository.setLastNotes()
        }
    }
}
