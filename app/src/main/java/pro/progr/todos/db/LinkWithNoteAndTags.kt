package pro.progr.todos.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlin.collections.List

data class LinkWithNoteAndTags (
    @Embedded
    val noteToNote: NoteToNote,

    @Relation(
        parentColumn = "note_from",
        entity = Note::class,
        entityColumn = "id"
    )
    val note: List<Note>,

    @Relation(
        parentColumn = "note_from",
        entity = NoteTag::class,
        entityColumn = "id",
        associateBy = Junction(
            value = NoteToTagXRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id")
    )
    val tags : List<NoteTag>
)