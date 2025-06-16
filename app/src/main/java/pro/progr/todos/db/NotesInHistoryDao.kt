package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface NotesInHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteInHistory: NoteInHistory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: List<NoteInHistory>)

//    @Transaction
//    suspend fun insertIfNotExist(notes: List<NoteInHistory>) {
        //if (getHistoryForDate(LocalDate.now().toEpochDay()).isEmpty()) {
            //insert(notes)
        //}
//    }

    @Delete
    fun delete(noteInHistory: NoteInHistory): Int

    @Update
    fun update(noteInHistory: NoteInHistory): Int

//    @Query("SELECT * FROM notes_in_history WHERE date = :date ORDER BY id DESC")
//    fun getHistoryForDate(date: Long): List<NoteInHistory>

    @Query("SELECT * FROM notes_in_history ORDER BY id DESC")
    fun getHistory(): Flow<List<NoteInHistoryWithData>>

    @RawQuery(observedEntities = [NoteInHistory::class])
    fun getHistoryByQuery(query: SupportSQLiteQuery) : Flow<List<NoteInHistoryWithData>>

}