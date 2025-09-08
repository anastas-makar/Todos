package pro.progr.todos

import androidx.room.withTransaction
import pro.progr.todos.api.SyncMetaData
import pro.progr.todos.api.TodosApiService
import pro.progr.todos.api.TodosSync
import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.DiamondsLogDao
import pro.progr.todos.db.NoteListsDao
import pro.progr.todos.db.NoteToTagXRefDao
import pro.progr.todos.db.NotesDao
import pro.progr.todos.db.NotesInHistoryDao
import pro.progr.todos.db.OutboxDao
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
    private val diamondsLogDao: DiamondsLogDao = db.diamondsLogDao()
    private val outBoxDao: OutboxDao = db.outBoxDao()

    suspend fun sync() {
        val outboxes = outBoxDao.getSync()

        val byTable: Map<String, List<String>> = outboxes
            .groupBy { it.tableName }
            .mapValues { entry -> entry.value.map { it.rowId }.distinct() }

        val syncData = TodosSync(
            syncMetaData = SyncMetaData(),
            notes = if (byTable["notes"].isNullOrEmpty()) emptyList()
                else notesDao.getUpdates(byTable["notes"]!!),
            notesInHistory = if (byTable["notes_in_history"].isNullOrEmpty()) emptyList()
                else notesInHistoryDao.getUpdates(byTable["notes_in_history"]!!),
            notesLists = if (byTable["note_lists"].isNullOrEmpty()) emptyList()
                else noteListsDao.getUpdates(byTable["note_lists"]!!),
            noteTags = if (byTable["note_tag"].isNullOrEmpty()) emptyList()
                else tagsDao.getUpdates(byTable["note_tag"]!!),
            noteToTags = if (byTable["note_to_tag"].isNullOrEmpty()) emptyList()
                else noteToTagXRefDao.getUpdates(byTable["note_to_tag"]!!),
            diamondLogs = diamondsLogDao.getSync()
        )

        val result = startServerSync(syncData)

        if (result.isSuccess) {
            val severData = result.getOrThrow()
            db.withTransaction {
                notesDao.setUpdates(severData.notes)
                notesInHistoryDao.setUpdates(severData.notesInHistory)
                noteListsDao.setUpdates(severData.notesLists)
                tagsDao.setUpdates(severData.noteTags)
                noteToTagXRefDao.setUpdates(severData.noteToTags)
                diamondsCountDao.setUpdates(severData.diamondLogs)
            }

            val finishResult = finishServerSync(SyncMetaData())

            if (finishResult.isSuccess) {
                db.withTransaction {
                    outBoxDao.clearSync(outboxes)
                    diamondsLogDao.clearSync(syncData.diamondLogs)
                }
            }
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