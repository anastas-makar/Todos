package pro.progr.todos.api.model

data class NoteToTagXRefDto (
    val id: String,
    val noteId: String,
    val tagId: String,
    val deleted: Boolean
)