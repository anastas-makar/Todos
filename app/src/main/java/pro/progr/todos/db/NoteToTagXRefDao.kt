package pro.progr.todos.db

import androidx.room.*
import javax.inject.Singleton

@Dao
@Singleton
interface NoteToTagXRefDao {

    @Query("SELECT * FROM note_to_tag WHERE updated_at > :lastUpdateTime")
    suspend fun getUpdates(lastUpdateTime : Long) : List<NoteToTagXRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NoteToTagXRef>)
}