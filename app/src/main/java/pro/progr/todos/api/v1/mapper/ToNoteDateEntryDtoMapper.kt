package pro.progr.todos.api.v1.mapper

import pro.progr.personalcrypto.PersonalCrypto
import pro.progr.todos.api.v1.model.NoteDateEntryDto
import pro.progr.todos.api.v1.model.SublistChainDto
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
        // These two legacy wire fields are sourced from the parent Note for server compatibility.
        reward = note.reward,
        sublistChain = SublistChainDto(note.sublistChain.sublistsString),
        style = ColorStyleConverter().fromColorStyle(entry.style),
        fillTitleBackground = entry.fillTitleBackground,
        fillTextBackground = entry.fillTextBackground,
        todo = entry.todo.name,
        edited = true,
        deleted = entry.deleted,
        latestUpdate = latestUpdate
    )
}
