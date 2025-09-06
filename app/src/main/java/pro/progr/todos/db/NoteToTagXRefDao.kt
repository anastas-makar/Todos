package pro.progr.todos.db

import androidx.room.*
import javax.inject.Singleton

@Dao
@Singleton
interface NoteToTagXRefDao {

    @Query("SELECT * FROM note_to_tag WHERE id IN (:uuids)")
    suspend fun getUpdates(uuids: List<String>) : List<NoteToTagXRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NoteToTagXRef>)
}