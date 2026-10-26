package pro.progr.todos.api.v1.mapper

import pro.progr.todos.api.v1.model.DiamondsLogDto
import pro.progr.todos.db.DiamondsLog

fun DiamondsLogDto.toEntity(): DiamondsLog {
    return DiamondsLog(
        uuid = uuid,
        createdAt = createdAt,
        day = day,
        count = count
    )
}