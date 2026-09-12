package pro.progr.todos.api.mapper

import com.google.gson.Gson
import pro.progr.todos.api.model.NoteInHistoryDto
import pro.progr.todos.api.model.SublistChainDto
import pro.progr.todos.db.ColorStyleConverter
import pro.progr.todos.db.NoteInHistory
import pro.progr.personalcrypto.PersonalCrypto

fun NoteInHistory.toDto(personalCrypto: PersonalCrypto, latestUpdate: Long?): NoteInHistoryDto {
    return NoteInHistoryDto(
        id = id,
        noteId = noteId,
        date = date,
        title = personalCrypto.encryptTodosValue(title),
        description = personalCrypto.encryptTodosValue(description),
        reward = reward,
        sublistChain = SublistChainDto(sublistChain.sublistsString),
        schedule = Gson().toJson(schedule), // сериализация Schedule в строку
        style = ColorStyleConverter().fromColorStyle(style),
        fillTitleBackground = fillTitleBackground,
        fillTextBackground = fillTextBackground,
        todo = todo.name,
        edited = edited,
        deleted = deleted,
        latestUpdate = latestUpdate
    )
}
