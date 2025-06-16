package pro.progr.doflow.db

import androidx.room.*

@Dao
interface NoteToTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ref: NoteToTagXRef) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(refs: List<NoteToTagXRef>) : List<Long>

    @Delete
    fun delete(ref: NoteToTagXRef) : Int

    @Query("DELETE FROM note_to_tag WHERE note_id=:noteId AND tag_id=:tagId")
    fun deleteByNoteAndTag(noteId : Long, tagId : Long)

    @Update
    fun update(ref: NoteToTagXRef) : Int
}