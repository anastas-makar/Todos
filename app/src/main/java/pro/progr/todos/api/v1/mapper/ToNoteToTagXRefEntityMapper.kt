package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.NoteToTagXRefDto
import pro.progr.todos.db.NoteToTagXRef

fun NoteToTagXRefDto.toEntity(): NoteToTagXRef {
    return NoteToTagXRef(
        id = id,
        noteId = noteId,
        tagId = tagId,
        deleted = deleted
    )
}

