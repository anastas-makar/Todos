package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NotesListDto
import pro.progr.todos.db.NotesList
import pro.progr.todos.db.SublistChain


fun NotesListDto.toEntity(): NotesList {
    return NotesList(
        id = id,
        title = title,
        isCurrent = isCurrent,
        sublistChain = SublistChain(sublistChain.sublistsString),
        deleted = deleted
    )
}