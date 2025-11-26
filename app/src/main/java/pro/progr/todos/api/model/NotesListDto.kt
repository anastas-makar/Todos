package pro.progr.todos.api.model

data class NotesListDto(
    val id: String,
    val title: String,
    val isCurrent: Boolean,
    val sublistChain: SublistChainDto,
    val deleted: Boolean,
    val latestUpdate: Long?
)