package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface NotesDao {

    @Query("SELECT * FROM notes WHERE id IN (:uuids)")
    suspend fun getUpdates(uuids : List<String>) : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<Note>)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteFlow(noteId: String): Flow<Note?>

    @Query("UPDATE notes SET sublist_chain = :sublistChain WHERE id = :noteId")
    fun updateSublistChain(noteId: String, sublistChain: String): Int
}