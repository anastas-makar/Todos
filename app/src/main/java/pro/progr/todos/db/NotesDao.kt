package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Singleton

@Dao
@Singleton
interface NotesDao {

    @Query("SELECT * FROM notes WHERE updated_at > :lastUpdateTime")
    suspend fun getUpdates(lastUpdateTime : Long) : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<Note>)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteFlow(noteId: String): Flow<Note?>

    @Query("UPDATE notes SET sublist_chain = :sublistChain, updated_at = :updatedAt WHERE id = :noteId")
    fun updateSublistChain(noteId: String, sublistChain: String,
                           updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)): Int
}