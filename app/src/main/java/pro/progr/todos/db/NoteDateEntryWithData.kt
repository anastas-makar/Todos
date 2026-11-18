package pro.progr.todos.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteDateEntryWithData(
    @Embedded val dateEntry: NoteDateEntry,
    @Relation(parentColumn = "noteId", entityColumn = "id")
    val note: Note,
    @Relation(
        parentColumn = "noteId",
        entity = NoteTag::class,
        entityColumn = "id",
        associateBy = Junction(
            value = NoteToTagXRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<NoteTag>
)
