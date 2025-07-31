package pro.progr.todos.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlin.collections.List

data class NoteInHistoryWithData (
    @Embedded
    val noteInHistory: NoteInHistory,

    @Relation(
        parentColumn = "noteId",
        entity = NoteTag::class,
        entityColumn = "id",
        associateBy = Junction(
            value = NoteToTagXRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id")
    )
    val tags : List<NoteTag>
)