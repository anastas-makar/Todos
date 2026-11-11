package pro.progr.todos.brightcards.model

/** Stable identity of one rendered note row in one list/calendar position. */
data class CardRowKey(
    val noteId: String,
    val epochDay: Long?
) {
    init {
        require(noteId.isNotBlank()) { "noteId must not be blank" }
    }

    /** ViewModelStore keys are strings, so length-prefix the ID to avoid ambiguity. */
    fun viewModelStoreKey(): String =
        "listed-card:${noteId.length}:$noteId:${epochDay?.toString() ?: "undated"}"
}
