package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.DiamondsLogDto
import pro.progr.todos.db.DiamondsLog

fun DiamondsLog.toDto(): DiamondsLogDto {
    return DiamondsLogDto(
        uuid = uuid,
        createdAt = createdAt,
        day = day,
        count = count
    )
}