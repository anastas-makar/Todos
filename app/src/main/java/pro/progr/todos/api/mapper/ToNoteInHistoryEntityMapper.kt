package pro.progr.todos.api.mapper

import com.google.gson.Gson
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.api.model.NoteInHistoryDto
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.db.ColorStyleConverter
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.SublistChain

fun NoteInHistoryDto.toEntity(): NoteInHistory {
    return NoteInHistory(
        id = id,
        noteId = noteId,
        date = date,
        title = title,
        description = description,
        reward = reward,
        sublistChain = SublistChain(sublistChain.sublistsString),
        schedule = Gson().fromJson(schedule, Schedule::class.java),
        style = ColorStyleConverter().toColorStyle(style),
        fillTitleBackground = fillTitleBackground,
        fillTextBackground = fillTextBackground,
        todo = TodoStatus.valueOf(todo),
        edited = edited,
        deleted = deleted
    )
}