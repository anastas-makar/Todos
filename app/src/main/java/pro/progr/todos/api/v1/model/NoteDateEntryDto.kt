package pro.progr.todos.api.v1.model

data class NoteDateEntryDto(
    val id: String,
    val noteId: String,
    val date: Long,
    val title: String,
    val description: String,
    val style: String,                 // ColorStyle → строка через конвертер
    val fillTitleBackground: Boolean,
    val fillTextBackground: Boolean,
    val todo: String,                  // enum.name
    val deleted: Boolean,
    val latestUpdate: Long?
)
