package pro.progr.todos.api.v1.mapper

import pro.progr.personalcrypto.PersonalCrypto
import pro.progr.todos.api.v1.model.NoteDateEntryDto
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.db.ColorStyleConverter
import pro.progr.todos.db.NoteDateEntry

fun NoteDateEntryDto.toEntity(personalCrypto: PersonalCrypto): NoteDateEntry = NoteDateEntry(
    id = id,
    noteId = noteId,
    date = date,
    title = personalCrypto.decryptTodosValue(title),
    description = personalCrypto.decryptTodosValue(description),
    style = ColorStyleConverter().toColorStyle(style),
    fillTitleBackground = fillTitleBackground,
    fillTextBackground = fillTextBackground,
    todo = when (TodoStatus.valueOf(todo)) {
        TodoStatus.TODO_NOT_ACTIVE, TodoStatus.FAIL -> TodoStatus.TODO
        else -> TodoStatus.valueOf(todo)
    },
    deleted = deleted
)
