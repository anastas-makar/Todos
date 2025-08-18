package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {

    @Query("SELECT * FROM note_tag WHERE updated_at > :lastUpdateTime")
    suspend fun getUpdates(lastUpdateTime : Long) : List<NoteTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NoteTag>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: NoteTag)

    @Query("SELECT * FROM note_tag WHERE deleted = 0 ORDER BY id DESC")
    fun getAllTags() : Flow<List<NoteTag>>

}