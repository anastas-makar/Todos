package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

@Dao
interface NoteWithDataDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesWithTags() : Flow<List<NoteWithData>>

    @RawQuery(observedEntities = [NoteWithData::class])
    fun getNotesWithTags(query: SupportSQLiteQuery) : Flow<List<NoteWithData>>

    @Query("SELECT * FROM note_to_note WHERE note_to=:noteToId")
    fun getLinkedToNote(noteToId : Long) : Flow<List<LinkWithNoteAndTags>>

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId : Long)

    @Query("DELETE FROM note_to_note WHERE note_from = :noteId OR note_to=:noteId")
    fun deleteNoteToNote(noteId : Long)

    @Query("DELETE FROM note_to_tag WHERE note_id = :noteId")
    fun deleteNoteToTag(noteId: Long)

    @Insert
    fun insertNoteToTags(notesToTags : List<NoteToTagXRef>)

    @Insert
    fun insertNoteToTag(notesToTag : NoteToTagXRef)

    @Transaction
    open fun deleteAllByNoteId(noteId: Long) {
        deleteNoteById(noteId)
        deleteNoteToNote(noteId)
        deleteNoteToTag(noteId)
    }

    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    fun getByIds(ids : List<Long>) : Flow<List<NoteWithData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Transaction
    open fun update(note: NoteWithData) {
        update(note.note)
        deleteNoteToTag(note.note.id)
        insertNoteToTags(note.tags.map { noteTag: NoteTag ->
            NoteToTagXRef(noteId = note.note.id, tagId = noteTag.id)
        })
    }

    open fun addNoteTag(tagId : Long, noteId : Long) {
        insertNoteToTag(NoteToTagXRef(noteId = noteId, tagId = tagId))
    }

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNote(noteId: Long): NoteWithData

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteFlow(noteId: Long): Flow<NoteWithData?>

    @Transaction
    fun deleteWithHistoryForDate(note: Note, date: Long) : Int {
        deleteInHistory(date, note.id)
        return delete(note)
    }

    @Query("DELETE FROM notes_in_history WHERE noteId = :noteId AND date = :date")
    fun deleteInHistory(date: Long, noteId: Long)

    @Delete
    fun delete(note: Note): Int

    @Query("DELETE FROM note_to_tag WHERE tag_id = :tagId AND note_id = :noteId")
    fun removeNoteTag(tagId: Long, noteId: Long)
}