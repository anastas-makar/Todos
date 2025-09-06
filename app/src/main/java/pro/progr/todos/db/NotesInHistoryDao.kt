package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface NotesInHistoryDao {

    @Query("SELECT * FROM notes_in_history WHERE id IN (:uuids)")
    suspend fun getUpdates(uuids : List<String>) : List<NoteInHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NoteInHistory>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: List<NoteInHistory>)

    @Query("SELECT * FROM notes_in_history WHERE deleted = 0 ORDER BY id DESC")
    fun getHistory(): Flow<List<NoteInHistoryWithData>>

    @RawQuery(observedEntities = [NoteInHistory::class])
    fun getHistoryByQuery(query: SupportSQLiteQuery) : Flow<List<NoteInHistoryWithData>>

}