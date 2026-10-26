package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.NoteToTagXRefDto
import pro.progr.todos.db.NoteToTagXRef

fun NoteToTagXRef.toDto(latestUpdate: Long?): NoteToTagXRefDto {
    return NoteToTagXRefDto(
        id = id,
        noteId = noteId,
        tagId = tagId,
        deleted = deleted,
        latestUpdate = latestUpdate
    )
}