package pro.progr.todos.db

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import pro.progr.notecastle.model.NotesQueryBuilder
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class NotesRepository @Inject constructor(
    initialQuery: NotesQuery,
    private val dao: NoteWithDataDao,
    private val noteDateEntriesDao: NoteDateEntriesDao
) {
    private val notesQueryFlow = MutableStateFlow(initialQuery)

    val notesListFlow: Flow<List<NoteWithData>> = notesQueryFlow.flatMapLatest { query ->
        if (query.isChanged()) dao.getNotesWithTags(
            SimpleSQLiteQuery(query.queryString, query.queryArguments.toTypedArray())
        ) else dao.getAllNotesWithTags()
    }

    /** Includes tombstones; a deleted dated entry still suppresses its template occurrence. */
    val dateEntriesFlow = noteDateEntriesDao.observeAll()

    private val _selectedTagsFlow: MutableStateFlow<MutableList<NoteTag>> = MutableStateFlow(mutableListOf())

    fun updateQuery(builder: NotesQueryBuilder) {
        notesQueryFlow.value = builder.getQuery()
        _selectedTagsFlow.update { ArrayList(builder.tags) }
    }

    suspend fun deleteEmptyNotes(): Int = dao.deleteEmptyNotes()

    fun getQueryBuilder(): NotesQueryBuilder = NotesQueryBuilder(notesQueryFlow.value)

    fun matchesDateEntryQuery(entry: NoteDateEntryWithData): Boolean {
        val query = notesQueryFlow.value
        val note = entry.note
        if (note.deleted) return false
        if (!note.sublistChain.sublistsString.startsWith(query.sublistChain.sublistsString)) return false
        if (query.tags.isNotEmpty() && entry.tags.none { tag -> query.tags.any { it.id == tag.id } }) return false
        if (query.filterType != null && (note.patternType ?: pro.progr.todos.datefilters.FilterType.NO_TERMS) != query.filterType) return false
        if (query.notDone && entry.dateEntry.todo == pro.progr.todos.brightcards.model.TodoStatus.DONE) return false
        val search = query.description
        return search.isEmpty() || (entry.dateEntry.title + " " + entry.dateEntry.description)
            .contains(search, ignoreCase = true)
    }
}
