package pro.progr.todos.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

@Dao
interface NoteWithDataDao {

    @Query("SELECT * FROM notes WHERE deleted = 0 ORDER BY id DESC")
    fun getAllNotesWithTags() : Flow<List<NoteWithData>>

    @RawQuery(observedEntities = [NoteWithData::class])
    fun getNotesWithTags(query: SupportSQLiteQuery) : Flow<List<NoteWithData>>

    @Query("UPDATE note_to_tag SET deleted = 1, note_id = \"\", tag_id = \"\" WHERE note_id = :noteId")
    fun deleteNoteToTag(noteId: String)

    @Insert
    fun insertNoteToTags(notesToTags : List<NoteToTagXRef>)

    @Insert
    fun insertNoteToTag(notesToTag : NoteToTagXRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Query("UPDATE notes SET title = :title WHERE id = :noteId")
    suspend fun updateTitle(noteId: String, title: String)

    @Query("UPDATE notes SET description = :description WHERE id = :noteId")
    suspend fun updateDescription(noteId: String, description: String)

    @Query("UPDATE notes SET style = :style WHERE id = :noteId")
    @TypeConverters(ColorStyleConverter::class)
    suspend fun updateStyle(noteId: String, style: pro.progr.todos.brightcards.colors.ColorStyle)

    @Query("UPDATE notes SET fillTitleBackground = :filled WHERE id = :noteId")
    suspend fun updateTitleBackground(noteId: String, filled: Boolean)

    @Query("UPDATE notes SET fillTextBackground = :filled WHERE id = :noteId")
    suspend fun updateDescriptionBackground(noteId: String, filled: Boolean)

    @Query("UPDATE notes SET reward = :reward WHERE id = :noteId")
    suspend fun updateReward(noteId: String, reward: Int)

    @Query("UPDATE notes SET todo = :todo WHERE id = :noteId")
    suspend fun updateTodo(noteId: String, todo: pro.progr.todos.brightcards.model.TodoStatus)

    @Transaction
    fun update(note: NoteWithData) {
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

    @Query("UPDATE notes_in_history SET deleted = 1 WHERE noteId = :noteId AND date = :date")
    fun deleteInHistory(date: Long, noteId: String)

    @Query("UPDATE notes SET deleted = 1 WHERE id = :noteId")
    fun delete(noteId: String): Int

    @Query("UPDATE note_to_tag SET deleted = 1, note_id = \"\", tag_id = \"\" WHERE tag_id = :tagId AND note_id = :noteId")
    fun removeNoteTag(tagId: String, noteId: String)
}
