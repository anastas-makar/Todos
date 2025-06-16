package pro.progr.brightcards

data class MenuItem (
    val text: String,
    val onClick: () -> Unit
)