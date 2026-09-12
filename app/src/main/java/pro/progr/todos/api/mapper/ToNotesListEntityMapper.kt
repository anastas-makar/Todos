package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NotesListDto
import pro.progr.todos.db.NotesList
import pro.progr.todos.db.SublistChain
import pro.progr.personalcrypto.PersonalCrypto


fun NotesListDto.toEntity(personalCrypto: PersonalCrypto): NotesList {
    return NotesList(
        id = id,
        title = personalCrypto.decryptTodosValue(title),
        isCurrent = isCurrent,
        sublistChain = SublistChain(sublistChain.sublistsString),
        deleted = deleted
    )
}
