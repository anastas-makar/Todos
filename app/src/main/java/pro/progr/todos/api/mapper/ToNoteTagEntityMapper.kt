package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.NoteTagDto
import pro.progr.todos.db.NoteTag
import pro.progr.personalcrypto.PersonalCrypto

fun NoteTagDto.toEntity(personalCrypto: PersonalCrypto): NoteTag {
    return NoteTag(
        id = id,
        title = personalCrypto.decryptTodosValue(title),
        deleted = deleted
    )
}
