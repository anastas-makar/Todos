package pro.progr.brightcards.model

data class CardTag(
    val id: Long,
    val title: String
) {
    override fun toString(): String {
        return title
    }
}
