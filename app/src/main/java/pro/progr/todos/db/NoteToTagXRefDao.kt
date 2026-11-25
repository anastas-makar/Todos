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

    @Query("SELECT * FROM note_to_tag WHERE tag_id IN (:tagIds) AND deleted = 0")
    suspend fun getActiveByTagIds(tagIds: List<String>): List<NoteToTagXRef>

    @Query("UPDATE note_to_tag SET tag_id = :tagId WHERE id = :xrefId AND deleted = 0")
    suspend fun reassignTag(xrefId: String, tagId: String)

    @Query("UPDATE note_to_tag SET deleted = 1, note_id = '', tag_id = '' WHERE id = :xrefId")
    suspend fun tombstone(xrefId: String)
}
