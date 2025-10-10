package pro.progr.todos.api.model

data class DiamondsLogDto (
    val uuid: String,
    val createdAt: Long,
    val day: Long,
    val count: Int
)