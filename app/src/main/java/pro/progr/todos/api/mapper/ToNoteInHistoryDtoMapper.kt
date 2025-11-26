package pro.progr.todos.api.mapper

import com.google.gson.Gson
import pro.progr.todos.api.model.NoteInHistoryDto
import pro.progr.todos.api.model.SublistChainDto
import pro.progr.todos.db.ColorStyleConverter
import pro.progr.todos.db.NoteInHistory

fun NoteInHistory.toDto(latestUpdate: Long?): NoteInHistoryDto {
    return NoteInHistoryDto(
        id = id,
        noteId = noteId,
        date = date,
        title = title,
        description = description,
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