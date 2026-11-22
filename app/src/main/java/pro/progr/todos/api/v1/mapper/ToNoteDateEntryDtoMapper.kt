package pro.progr.todos.api.v1.mapper

import pro.progr.personalcrypto.PersonalCrypto
import pro.progr.todos.api.v1.model.NoteDateEntryDto
import pro.progr.todos.db.ColorStyleConverter
import pro.progr.todos.db.NoteDateEntryWithData

fun NoteDateEntryWithData.toDto(personalCrypto: PersonalCrypto, latestUpdate: Long?): NoteDateEntryDto {
    val entry = dateEntry
    return NoteDateEntryDto(
        id = entry.id,
        noteId = entry.noteId,
        date = entry.date,
        title = personalCrypto.encryptTodosValue(entry.title),
        description = personalCrypto.encryptTodosValue(entry.description),
        style = ColorStyleConverter().fromColorStyle(entry.style),
        fillTitleBackground = entry.fillTitleBackground,
        fillTextBackground = entry.fillTextBackground,
        todo = entry.todo.name,
        deleted = entry.deleted,
        latestUpdate = latestUpdate
    )
}
