package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.NoteTagDto
import pro.progr.todos.db.NoteTag
import pro.progr.personalcrypto.PersonalCrypto

fun NoteTagDto.toEntity(personalCrypto: PersonalCrypto): NoteTag {
    return NoteTag(
        id = id,
        title = personalCrypto.decryptTodosValue(title),
        deleted = deleted
    )
}
