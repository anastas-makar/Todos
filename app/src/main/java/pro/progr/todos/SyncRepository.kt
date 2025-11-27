package pro.progr.todos

import android.util.Log
import androidx.room.Transaction
import androidx.room.withTransaction
import com.google.gson.Gson
import pro.progr.todos.api.SyncMetaData
import pro.progr.todos.api.TodosApiService
import pro.progr.todos.api.TodosSync
import pro.progr.todos.api.mapper.toDto
import pro.progr.todos.api.mapper.toEntity
import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.DiamondsLog
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

    private val gson : Gson = Gson()

    suspend fun sync() {
        val diamondsLogs = compressDiamondLogs()

        val outboxes = outBoxDao.getSync()

        val byTable: Map<String, List<String>> = outboxes
            .groupBy { it.tableName }
            .mapValues { entry -> entry.value.map { it.rowId }.distinct() }

        val latestUpdates = outboxes.fold(HashMap<String, Long>()) { acc, o ->
            acc[o.rowId] = maxOf(acc[o.rowId] ?: Long.MIN_VALUE, o.createdAt)
            acc
        }

        val syncData = TodosSync(
            syncMetaData = SyncMetaData(dbVersion =  db.openHelper.readableDatabase.version),
            notes = if (byTable["notes"].isNullOrEmpty()) emptyList()
                else notesDao.getUpdates(byTable["notes"]!!).map { it.toDto(gson, latestUpdates[it.id]) },
            notesInHistory = if (byTable["notes_in_history"].isNullOrEmpty()) emptyList()
                else notesInHistoryDao.getUpdates(byTable["notes_in_history"]!!).map { it.toDto(latestUpdates[it.id]) },
            notesLists = if (byTable["note_lists"].isNullOrEmpty()) emptyList()
                else noteListsDao.getUpdates(byTable["note_lists"]!!).map { it.toDto(latestUpdates[it.id]) },
            noteTags = if (byTable["note_tag"].isNullOrEmpty()) emptyList()
                else tagsDao.getUpdates(byTable["note_tag"]!!).map { it.toDto(latestUpdates[it.id]) },
            noteToTags = if (byTable["note_to_tag"].isNullOrEmpty()) emptyList()
                else noteToTagXRefDao.getUpdates(byTable["note_to_tag"]!!).map { it.toDto(latestUpdates[it.id]) },
            diamondLogs = diamondsLogs.map { it.toDto() }
        )

        Log.wtf("SYNC DATA", syncData.toString())

        val result = startServerSync(syncData)

        if (result.isSuccess) {
            val severData = result.getOrThrow()
            Log.wtf("SERVER DATA", severData.toString())
            db.withTransaction {
                if (!severData.notes.isNullOrEmpty()) notesDao.setUpdates(
                    severData.notes.map { it.toEntity(gson) })
                if (!severData.notesInHistory.isNullOrEmpty()) notesInHistoryDao.setUpdates(
                    severData.notesInHistory.map { it.toEntity() })
                if (!severData.notesLists.isNullOrEmpty()) noteListsDao.setUpdates(
                    severData.notesLists.map { it.toEntity() })
                if (!severData.noteTags.isNullOrEmpty()) tagsDao.setUpdates(
                    severData.noteTags.map { it.toEntity() })
                if (!severData.noteToTags.isNullOrEmpty()) noteToTagXRefDao.setUpdates(
                    severData.noteToTags.map { it.toEntity() })
            }

            val finishResult = finishServerSync(SyncMetaData(dbVersion =  db.openHelper.readableDatabase.version))

            if (finishResult.isSuccess) {
                db.withTransaction {
                    Log.wtf("OUTBOXES", outboxes.toString())
                    outBoxDao.clearSync(outboxes)
                    //применяются только после успешного /finish, чтобы избежать дублей
                    if (!severData.diamondLogs.isNullOrEmpty()) diamondsCountDao.setUpdates(
                        severData.diamondLogs.map { it.toEntity() })
                    diamondsLogDao.clear(
                        syncData.diamondLogs.map { it.toEntity() })
                }

                Log.wtf("SUCCESS", "SUCCESS")
            }
            Log.wtf("wtf", finishResult.toString())
        } else {
            Log.wtf("RESULT IS NOT SUCCESS", result.toString())
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

    @Transaction
    suspend fun compressDiamondLogs() : List<DiamondsLog> {
        val allLogs = diamondsLogDao.getAll() // Получаем все локальные логи
        if (allLogs.isEmpty()) return emptyList()

        val grouped = allLogs.groupBy { it.day }.map { (day, logs) ->
            DiamondsLog(
                uuid = logs.maxOf { it.uuid },//пусть будет конкретный uuid, чтобы застраховаться от повторной вставки
                day = day,
                count = logs.sumOf { it.count }
            )
        }

        // Удаляем старые строго по UUID — чтобы не задеть новые, если вдруг совпадения по дням
        diamondsLogDao.clear(allLogs)

        // Вставляем новые «ужатые» записи
        diamondsLogDao.insert(grouped)

        return grouped
    }

    @Transaction
    suspend fun compressSink() {
        val outboxes = outBoxDao.getSync()
        if (outboxes.isEmpty()) return

        // Группируем по (tableName, rowId) и берём самый свежий createdAt
        val shrinkedOutboxes = outboxes
            .groupBy { it.tableName to it.rowId }
            .map { (_, list) ->
                list.maxBy { it.createdAt }  // оставляем одну — самую новую
            }

        // Удаляем все старые записи (по id)
        outBoxDao.clearSync(outboxes)

        // Вставляем сжатый набор
        outBoxDao.insert(shrinkedOutboxes)
    }

    suspend fun shrink() {
        compressDiamondLogs()
        compressSink()
    }
}