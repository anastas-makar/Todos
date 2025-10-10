package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteTagDto
import pro.progr.todos.db.NoteTag

fun NoteTagDto.toEntity(): NoteTag {
    return NoteTag(
        id = id,
        title = title,
        deleted = deleted
    )
}