package pro.progr.todos.api.mapper

import com.google.gson.Gson
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.api.model.NoteDto
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.Note
import pro.progr.todos.db.ArrayPOJO
import pro.progr.todos.db.ColorStyleConverter
import java.util.LinkedHashMap

fun NoteDto.toEntity(gson: Gson = Gson()): Note {
    val styleObj = ColorStyleConverter().toColorStyle(style)

    // восстановление patternDates
    val patternDays = LinkedHashMap<Long, Schedule.Day>().apply {
        patterDates.forEach { (key, dto) ->
            put(key, Schedule.Day(active = dto.active, longValue = dto.longValue ?: 0))
        }
    }

    val entity = Note(
        id = id,
        date = date,
        title = title,
        description = description,
        sublistChain = pro.progr.todos.db.SublistChain(sublistChain.sublistsString),
        reward = reward,
        addedDates = ArrayPOJO(addedDates.toTypedArray()),
        cancelledDates = ArrayPOJO(cancelledDates.toTypedArray()),
        patterDates = patternDays,
        patternType = FilterType.valueOf(patternType),
        dateSince = dateSince,
        dateTill = dateTill,
        style = styleObj,
        fillTitleBackground = fillTitleBackground,
        fillTextBackground = fillTextBackground,
        todo = TodoStatus.valueOf(todo),
        latestDone = latestDone,
        deleted = deleted
    )

    // schedule из JSON-строки, если есть
    schedule?.let {
        entity.schedule = gson.fromJson(it, Schedule::class.java)
    }

    return entity
}