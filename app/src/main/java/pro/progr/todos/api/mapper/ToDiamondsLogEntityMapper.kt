package pro.progr.todos.api.mapper

import pro.progr.todos.api.model.DiamondsLogDto
import pro.progr.todos.db.DiamondsLog

fun DiamondsLogDto.toEntity(): DiamondsLog {
    return DiamondsLog(
        uuid = uuid,
        createdAt = createdAt,
        day = day,
        count = count
    )
}