package pro.progr.todos.brightcards.model

data class CardTag(
    val id: String,
    val title: String
) {
    override fun toString(): String {
        return title
    }
}
