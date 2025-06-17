package pro.progr.todos

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
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteWithData
import pro.progr.todos.db.NotesRepository
import pro.progr.todos.db.SublistChain
import java.time.LocalDate
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CardsListViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    val cardsList: MutableState<List<ListedCardViewModel>> = mutableStateOf(emptyList())

    val selectedTag: MutableState<NoteTag?> = mutableStateOf(null)

    val notifications: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    val cardsForDates: HashMap<LocalDate, MutableStateFlow<List<ListedCardViewModel>>> = HashMap()

    private lateinit var viewModelCreator: (NoteWithData, CardContent, LocalDate?) -> ListedCardViewModel

    fun setViewModelCreator(creator: (NoteWithData, CardContent, LocalDate?) -> ListedCardViewModel) {
        viewModelCreator = creator
    }

    init {
        observeCards()
    }

    private fun observeCards() {

        viewModelScope.launch {

            notesRepository.notesListFlow.collectLatest { notesList ->

                cardsList.value = notesList.map { note ->

                    val cardContent = NoteConverter.toCardContent(note)

                    if (note.note.latestDone == LocalDate.now().toEpochDay()) {
                        cardContent.todo = TodoStatus.DONE
                    }

                    viewModelCreator(
                        note,
                        cardContent,
                        null
                    )
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
                            note.note.schedule.pattern.type.datesFilter.isActual(
                                note.note.schedule,
                                date
                            )
                        }.map { note ->
                            val cardContent = NoteConverter.toCardContent(note)

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
                                note,
                                cardContent,
                                date
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

            viewModelCreator(
                NoteWithData(
                    note = NoteConverter.toNote(cardContent),
                    tags = cardContent.tags.map { tag -> NoteTag(tag.id, tag.title) },
                    images = emptyList()
                ),
                cardContent,
                date
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

    fun useList(sublistChain: SublistChain) {
        notesRepository.updateQuery(
            notesRepository.getQueryBuilder().withSublistChain(sublistChain)
        )
    }

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
                    notifications.value + listOf("Удалено пустых заметок: $deletedCount")
            }
        }
    }

    fun saveTodaysNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            notesRepository.setLastNotes()
        }
    }
}