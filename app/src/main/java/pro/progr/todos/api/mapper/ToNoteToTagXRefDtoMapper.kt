package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteToTagXRefDto
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