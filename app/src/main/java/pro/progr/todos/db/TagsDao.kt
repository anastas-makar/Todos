package pro.progr.doflow.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: NoteTag) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tags: List<NoteTag>) : List<Long>

    @Delete
    fun delete(tag: NoteTag) : Int

    @Update
    fun update(tag: NoteTag) : Int

    @Query("SELECT * FROM note_tag ORDER BY id DESC")
    fun getAllTags() : Flow<List<NoteTag>>

    @Query("select note_tag.* from note_to_tag, note_tag where note_to_tag.note_id=:noteId AND note_to_tag.tag_id = note_tag.id")
    fun getTagsByNoteId(noteId : Long): Flow<List<NoteTag>>

}