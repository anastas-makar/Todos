package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface NotesInHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: List<NoteInHistory>)

    @Query("SELECT * FROM notes_in_history ORDER BY id DESC")
    fun getHistory(): Flow<List<NoteInHistoryWithData>>

    @RawQuery(observedEntities = [NoteInHistory::class])
    fun getHistoryByQuery(query: SupportSQLiteQuery) : Flow<List<NoteInHistoryWithData>>

}