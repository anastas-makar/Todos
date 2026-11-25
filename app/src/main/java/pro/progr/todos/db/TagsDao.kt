package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {

    @Query("SELECT * FROM note_tag WHERE id IN (:uuids)")
    suspend fun getUpdates(uuids: List<String>) : List<NoteTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NoteTag>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: NoteTag)

    @Update
    suspend fun update(tag: NoteTag)

    @Query("SELECT * FROM note_tag WHERE deleted = 0")
    suspend fun getActiveTags(): List<NoteTag>

    @Query("SELECT * FROM note_tag")
    suspend fun getAllTagsOnce(): List<NoteTag>

    @Query("SELECT * FROM note_tag WHERE id = :id AND deleted = 0 LIMIT 1")
    suspend fun getActiveTag(id: String): NoteTag?

    @Query("SELECT * FROM note_tag WHERE deleted = 0 ORDER BY id DESC")
    fun getAllTags() : Flow<List<NoteTag>>

}
