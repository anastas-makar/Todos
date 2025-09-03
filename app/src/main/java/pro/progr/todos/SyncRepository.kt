package pro.progr.todos

import androidx.room.withTransaction
import pro.progr.todos.api.SyncMetaData
import pro.progr.todos.api.TodosApiService
import pro.progr.todos.api.TodosSync
import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.NoteListsDao
import pro.progr.todos.db.NoteToTagXRefDao
import pro.progr.todos.db.NotesDao
import pro.progr.todos.db.NotesInHistoryDao
import pro.progr.todos.db.TagsDao
import pro.progr.todos.db.TodosDataBase
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val db: TodosDataBase,
    private val apiService: TodosApiService
) {
    private val notesDao: NotesDao = db.notesDao()
    private val noteListsDao: NoteListsDao = db.noteListsDao()
    private val tagsDao: TagsDao = db.tagsDao()
    private val notesInHistoryDao: NotesInHistoryDao = db.notesInHistoryDao()
    private val diamondsCountDao: DiamondsCountDao = db.diamondsCountDao()
    private val noteToTagXRefDao: NoteToTagXRefDao = db.noteToTagXRefDao()

    suspend fun sync(lastUpdateTime : Long) {
        val syncData = TodosSync(
            syncMetaData = SyncMetaData(),
            notes = notesDao.getUpdates(lastUpdateTime),
            notesInHistory = notesInHistoryDao.getUpdates(lastUpdateTime),
            notesLists = noteListsDao.getUpdates(lastUpdateTime),
            noteTags = tagsDao.getUpdates(lastUpdateTime),
            noteToTags = noteToTagXRefDao.getUpdates(lastUpdateTime),
            diamondCounts = diamondsCountDao.getUpdates(lastUpdateTime)
        )

        val result = startServerSync(syncData)

        if (result.isSuccess) {
            val data = result.getOrThrow()
            db.withTransaction {
                notesDao.setUpdates(data.notes)
                notesInHistoryDao.setUpdates(data.notesInHistory)
                noteListsDao.setUpdates(data.notesLists)
                tagsDao.setUpdates(data.noteTags)
                noteToTagXRefDao.setUpdates(data.noteToTags)
                diamondsCountDao.setUpdates(data.diamondCounts)
            }

            finishServerSync(SyncMetaData())
        }


    }

    private suspend fun startServerSync(payload: TodosSync): Result<TodosSync> = runCatching {
        val resp = apiService.syncStart(payload)
        if (resp.isSuccessful) {
            resp.body() ?: error("Empty body")
        } else {
            error("HTTP ${resp.code()}: ${resp.errorBody()?.string().orEmpty()}")
        }
    }

    private suspend fun finishServerSync(payload: SyncMetaData): Result<Boolean> = runCatching {
        val resp = apiService.syncFinish(payload)
        if (resp.isSuccessful) {
            resp.body() ?: error("Empty body")
        } else {
            error("HTTP ${resp.code()}: ${resp.errorBody()?.string().orEmpty()}")
        }
    }
}