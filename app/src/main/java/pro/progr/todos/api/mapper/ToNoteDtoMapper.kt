package pro.progr.todos.api.mapper

import com.google.gson.Gson
import pro.progr.todos.api.model.NoteDto
import pro.progr.todos.api.model.ScheduleDayDto
import pro.progr.todos.api.model.SublistChainDto
import pro.progr.todos.db.Note
import pro.progr.todos.db.ColorStyleConverter

fun Note.toDto(gson: Gson = Gson()): NoteDto {
    val styleString = ColorStyleConverter().fromColorStyle(style)

    return NoteDto(
        id = id,
        date = date,
        title = title,
        description = description,
        sublistChain = SublistChainDto(sublistChain.sublistsString),
        reward = reward,
        addedDates = addedDates?.longArray?.toList() ?: emptyList(),
        cancelledDates = cancelledDates?.longArray?.toList() ?: emptyList(),
        patterDates = patterDates?.mapValues { (_, day) ->
            ScheduleDayDto(
                active = day.active,
                longValue = day.longValue
            )
        } ?: emptyMap(),
        patternType = patternType?.name ?: "NO_TERMS",
        dateSince = dateSince,
        dateTill = dateTill,
        style = styleString,
        fillTitleBackground = fillTitleBackground,
        fillTextBackground = fillTextBackground,
        todo = todo.name,
        latestDone = latestDone,
        deleted = deleted,
        schedule = gson.toJson(schedule) // сериализуем schedule в JSON-строку
    )
}