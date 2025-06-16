package pro.progr.todos.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteWithNotesDao {

    @Query("SELECT * FROM notes WHERE id=:noteId")
    abstract fun getNoteWithLinkedNotes(noteId : Long) : Flow<NoteWithNotes>
}