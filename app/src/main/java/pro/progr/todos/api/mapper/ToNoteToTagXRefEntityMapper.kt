package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteToTagXRefDto
import pro.progr.todos.db.NoteToTagXRef

fun NoteToTagXRefDto.toEntity(): NoteToTagXRef {
    return NoteToTagXRef(
        id = id,
        noteId = noteId,
        tagId = tagId,
        deleted = deleted
    )
}

