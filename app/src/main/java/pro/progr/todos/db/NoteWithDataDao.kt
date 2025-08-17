package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.List

@Dao
interface NoteWithDataDao {

    @Query("SELECT * FROM notes WHERE deleted = 0 ORDER BY id DESC")
    fun getAllNotesWithTags() : Flow<List<NoteWithData>>

    @RawQuery(observedEntities = [NoteWithData::class])
    fun getNotesWithTags(query: SupportSQLiteQuery) : Flow<List<NoteWithData>>

    @Query("UPDATE note_to_tag SET deleted = 1, note_id = \"\", updated_at = :updatedAt WHERE note_id = :noteId")
    fun deleteNoteToTag(noteId: String,
                        updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Insert
    fun insertNoteToTags(notesToTags : List<NoteToTagXRef>)

    @Insert
    fun insertNoteToTag(notesToTag : NoteToTagXRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Transaction
    fun update(note: NoteWithData) {
        note.note.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        update(note.note)
        deleteNoteToTag(note.note.id)
        insertNoteToTags(note.tags.map { noteTag: NoteTag ->
            NoteToTagXRef(noteId = note.note.id, tagId = noteTag.id)
        })
    }

    fun addNoteTag(tagId : String, noteId : String) {
        insertNoteToTag(NoteToTagXRef(noteId = noteId, tagId = tagId))
    }

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNote(noteId: String): NoteWithData

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteFlow(noteId: String): Flow<NoteWithData?>

    @Transaction
    fun deleteWithHistoryForDate(note: Note, date: Long) : Int {
        deleteInHistory(date, note.id)
        return delete(note.id)
    }

    @Query("UPDATE notes_in_history SET deleted = 1, updated_at = :updatedAt WHERE noteId = :noteId AND date = :date")
    fun deleteInHistory(date: Long, noteId: String,
                        updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Query("UPDATE notes SET deleted = 1, updated_at = :updatedAt WHERE id = :noteId")
    fun delete(noteId: String,
               updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)): Int

    @Query("UPDATE note_to_tag SET deleted = 1, note_id = \"\", updated_at = :updatedAt WHERE tag_id = :tagId AND note_id = :noteId")
    fun removeNoteTag(tagId: String, noteId: String,
                      updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
}