package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NotesListDto
import pro.progr.todos.api.model.SublistChainDto
import pro.progr.todos.db.NotesList

fun NotesList.toDto(): NotesListDto {
    return NotesListDto(
        id = id,
        title = title,
        isCurrent = isCurrent,
        sublistChain = SublistChainDto(sublistChain.sublistsString),
        deleted = deleted
    )
}