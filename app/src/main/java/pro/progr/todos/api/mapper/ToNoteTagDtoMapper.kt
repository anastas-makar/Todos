package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteTagDto
import pro.progr.todos.db.NoteTag

fun NoteTag.toDto(latestUpdate: Long?): NoteTagDto {
    return NoteTagDto(
        id = id,
        title = title,
        deleted = deleted,
        latestUpdate = latestUpdate
    )
}