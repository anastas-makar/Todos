package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton
import kotlin.collections.List

@Dao
@Singleton
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notes: List<Note>): List<Long>

    @Delete
    fun delete(note: Note): Int

    @Update
    fun update(note: Note): Int

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteFlow(noteId: String): Flow<Note?>

    @Query("UPDATE notes SET sublist_chain = :sublistChain WHERE id = :noteId")
    fun updateSublistChain(noteId: String, sublistChain: String): Int
}