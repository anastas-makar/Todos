package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.DiamondsLogDto
import pro.progr.todos.db.DiamondsLog

fun DiamondsLog.toDto(): DiamondsLogDto {
    return DiamondsLogDto(
        uuid = uuid,
        createdAt = createdAt,
        day = day,
        count = count
    )
}