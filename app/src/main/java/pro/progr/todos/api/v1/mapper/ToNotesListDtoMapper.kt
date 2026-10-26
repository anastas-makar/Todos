package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.NotesListDto
import pro.progr.todos.api.v1.model.SublistChainDto
import pro.progr.todos.db.NotesList
import pro.progr.personalcrypto.PersonalCrypto

fun NotesList.toDto(personalCrypto: PersonalCrypto, latestUpdate: Long?): NotesListDto {
    return NotesListDto(
        id = id,
        title = personalCrypto.encryptTodosValue(title),
        isCurrent = isCurrent,
        sublistChain = SublistChainDto(sublistChain.sublistsString),
        deleted = deleted,
        latestUpdate = latestUpdate
    )
}
