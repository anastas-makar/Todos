package pro.progr.todos.api.v1.model

data class NoteToTagXRefDto (
    val id: String,
    val noteId: String,
    val tagId: String,
    val deleted: Boolean,
    val latestUpdate: Long?
)