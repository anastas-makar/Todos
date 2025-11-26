package pro.progr.todos.api.model

data class NoteInHistoryDto(
    val id: String,
    val noteId: String,
    val date: Long,
    val title: String,
    val description: String,
    val reward: Int,
    val sublistChain: SublistChainDto,
    val schedule: String,              // строка (json), как на сервере
    val style: String,                 // ColorStyle → строка через конвертер
    val fillTitleBackground: Boolean,
    val fillTextBackground: Boolean,
    val todo: String,                  // enum.name
    val edited: Boolean,
    val deleted: Boolean,
    val latestUpdate: Long?
)