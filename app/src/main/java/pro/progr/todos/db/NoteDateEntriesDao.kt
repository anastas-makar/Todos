package pro.progr.todos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDateEntriesDao {
    @Query("SELECT * FROM note_date_entries WHERE id IN (:ids)")
    suspend fun getUpdates(ids: List<String>): List<NoteDateEntry>

    @Transaction
    @Query("SELECT * FROM note_date_entries WHERE id IN (:ids)")
    suspend fun getUpdatesWithData(ids: List<String>): List<NoteDateEntryWithData>

    @Transaction
    @Query("SELECT * FROM note_date_entries WHERE noteId = :noteId AND date = :date LIMIT 1")
    suspend fun getDateEntryWithData(noteId: String, date: Long): NoteDateEntryWithData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates: List<NoteDateEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: NoteDateEntry)

    @Transaction
    @Query("SELECT * FROM note_date_entries ORDER BY date DESC, noteId")
    fun observeAll(): Flow<List<NoteDateEntryWithData>>

}
