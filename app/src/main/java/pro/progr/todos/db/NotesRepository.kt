package pro.progr.doflow.db

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import pro.progr.brightcards.model.CardContent
import pro.progr.doflow.NoteConverter
import pro.progr.doflow.NoteInHistoryConverter
import pro.progr.notecastle.model.NotesQueryBuilder
import java.time.LocalDate
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NotesRepository @Inject constructor(
    initialQuery : NotesQuery,
    private val dao: NoteWithDataDao,
    private val notesDao : NotesDao,
    private val notesInHistoryDao: NotesInHistoryDao,
    private val noteAndHistoryDao: NoteAndHistoryDao
) {
    private val notesQueryFlow : MutableStateFlow<NotesQuery>

    private val _notesListFlow : Flow<List<NoteWithData>>
    val notesListFlow get() = _notesListFlow

    private val _notesInHistoryFlow : Flow<Map<Long, List<CardContent>>>
    val notesInHistoryFlow get() = _notesInHistoryFlow

    private val _selectedTagsFlow : MutableStateFlow<MutableList<NoteTag>> = MutableStateFlow(mutableListOf())

    init {
        notesQueryFlow = MutableStateFlow(initialQuery)

        _notesListFlow = notesQueryFlow.flatMapLatest { notesQuery ->
            if(notesQuery.isChanged()) {
                dao.getNotesWithTags(
                    SimpleSQLiteQuery(
                        notesQuery.queryString,
                        notesQuery.queryArguments.toArray()
                    )
                )
            } else {
                dao.getAllNotesWithTags()
            }
        }

        _notesInHistoryFlow = notesQueryFlow.flatMapLatest { notesQuery ->
            val currentDate = LocalDate.now().toEpochDay()
            if(notesQuery.isChanged()) {
                notesInHistoryDao.getHistoryByQuery(
                    SimpleSQLiteQuery(
                        notesQuery.historyQueryString,
                        notesQuery.queryArguments.toArray()
                    )
                ).map {
                    it.filter { noteInHistory ->
                        noteInHistory.noteInHistory.date <= currentDate
                    }.groupBy { noteInHistory ->
                        noteInHistory.noteInHistory.date
                    }.mapValues { (_, notes) ->
                        notes.map { note ->
                            NoteInHistoryConverter.toCardContent(note)
                        }
                    }
                }
            } else {
                notesInHistoryDao.getHistory().map {
                    it.filter { noteInHistory ->
                        noteInHistory.noteInHistory.date <= currentDate
                    }.groupBy { noteInHistory ->
                        noteInHistory.noteInHistory.date
                    }.mapValues { (_, notes) ->
                        notes.map { note ->
                            NoteInHistoryConverter.toCardContent(note)
                        }
                    }
                }
            }
        }

    }

    fun updateQuery(builder : NotesQueryBuilder) {
        notesQueryFlow.value = builder.getQuery()

        _selectedTagsFlow.update { tagsList ->
            ArrayList(builder.tags)
        }
    }

    fun deleteUnlinkedHistoryForToday(today : Long) : Int {
        return noteAndHistoryDao.deleteEmptyNotesAndUnlinkedHistoryForToday(today)
    }

    fun getQueryBuilder() : NotesQueryBuilder {
        return NotesQueryBuilder(notesQueryFlow.value)
    }

    fun updateNote(note: Note) {
        notesDao.update(note)
    }

    suspend fun setTodaysNotes() {
        notesListFlow.first { notesList ->
            val notesInHistory = notesList.filter { note ->
                note.note.schedule.pattern.type.datesFilter.isActual(
                    note.note.schedule,
                    LocalDate.now()
                )
            }.map {
                    note -> NoteConverter.toNoteInHistory(note.note, LocalDate.now().toEpochDay())
            }

            saveNotesInHistory(notesInHistory)

            return@first true
        }
    }

    suspend fun setLastNotes() {

        val latestDate = noteAndHistoryDao.getLatestDate()

        val currentDate = LocalDate.now().toEpochDay()

        //if (currentDate == latestDate) return

        notesListFlow.first { notesList ->

            val dates : List<Long> = if (latestDate == null) listOf(currentDate) else
                (latestDate .. currentDate).toList()

            val notesInHistory = ArrayList<NoteInHistory>()

            for (date: Long in dates) {
                notesInHistory.addAll(

                    notesList.filter { note ->
                        note.note.schedule.pattern.type.datesFilter.isActual(
                            note.note.schedule,
                            LocalDate.ofEpochDay(date)
                        )
                    }.map {
                            note -> NoteConverter.toNoteInHistory(note.note, date)
                    })

            }

            saveNotesInHistory(notesInHistory)

            return@first true
        }
    }

    suspend fun saveNotesInHistory(notesInHistory : List<NoteInHistory>) {
        notesInHistoryDao.insert(notesInHistory)
    }
}