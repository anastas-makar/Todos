package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteTagDto
import pro.progr.todos.db.NoteTag
import pro.progr.personalcrypto.PersonalCrypto

fun NoteTag.toDto(personalCrypto: PersonalCrypto, latestUpdate: Long?): NoteTagDto {
    return NoteTagDto(
        id = id,
        title = personalCrypto.encryptTodosValue(title),
        deleted = deleted,
        latestUpdate = latestUpdate
    )
}
