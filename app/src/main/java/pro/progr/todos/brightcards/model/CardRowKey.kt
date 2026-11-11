package pro.progr.todos.brightcards.model

/** Stable identity of one rendered note row in one list/calendar position. */
data class CardRowKey(
    val noteId: String,
    val epochDay: Long?
) {
    init {
        require(noteId.isNotBlank()) { "noteId must not be blank" }
    }

    /** A unique, Bundle-saveable key for Compose lists. */
    fun stableStringKey(): String =
        "card-row:${noteId.length}:$noteId:${epochDay?.toString() ?: "undated"}"
}
