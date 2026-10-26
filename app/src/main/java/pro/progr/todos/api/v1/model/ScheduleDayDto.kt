package pro.progr.todos.api.v1.model

data class ScheduleDayDto (
    val longValue: Long,
    val active: Boolean = true)