package pro.progr.doflow.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteToNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNoteToNoteLink(noteToNote: NoteToNote) : Long

    @Query("SELECT * FROM note_to_note WHERE note_to = :noteId")
    abstract fun getNoteLinks(noteId : Long) : Flow<List<NoteToNote>>

    @Query("DELETE FROM note_to_note WHERE note_to = :noteTo AND note_from = :noteFrom")
    abstract fun deleteNoteLink(noteTo: Long, noteFrom: Long)
}