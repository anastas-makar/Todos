package pro.progr.todos

import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.NoteListsDao
import pro.progr.todos.db.NoteToTagXRefDao
import pro.progr.todos.db.NotesDao
import pro.progr.todos.db.NotesInHistoryDao
import pro.progr.todos.db.TagsDao
import pro.progr.todos.db.TodosDataBase
import javax.inject.Inject

class SyncRepository @Inject constructor(
    val notesDao: NotesDao,
    val noteListsDao: NoteListsDao,
    val tagsDao: TagsDao,
    val notesInHistoryDao: NotesInHistoryDao,
    val diamondsCountDao: DiamondsCountDao,
    val noteToTagXRefDao: NoteToTagXRefDao,
    val db: TodosDataBase
) {
    suspend fun sync(lastUpdateTime : Long) {
        val sync = TodosSync(
            notes = notesDao.getUpdates(lastUpdateTime),
            notesInHistory = notesInHistoryDao.getUpdates(lastUpdateTime),
            notesLists = noteListsDao.getUpdates(lastUpdateTime),
            noteTags = tagsDao.getUpdates(lastUpdateTime),
            noteToTags = noteToTagXRefDao.getUpdates(lastUpdateTime),
            diamondCounts = diamondsCountDao.getUpdates(lastUpdateTime)
        )


    }
}