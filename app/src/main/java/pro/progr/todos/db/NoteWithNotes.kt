package pro.progr.todos.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlin.collections.List

data class NoteWithNotes (
    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id",
        entity = Note::class,
        entityColumn = "id",
        associateBy = Junction(
            value = NoteToNote::class,
            parentColumn = "note_to",
            entityColumn = "note_from") //к одной note_to привязывается много note_from
    )
    val noteLinks : List<Note>
)