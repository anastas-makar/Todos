package pro.progr.todos.api.v1.model

data class NoteTagDto (
    val id: String,
    val title: String,
    val deleted: Boolean,
    val latestUpdate: Long?
)