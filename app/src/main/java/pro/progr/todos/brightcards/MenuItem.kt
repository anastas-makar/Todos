package pro.progr.todos.brightcards

data class MenuItem (
    val text: String,
    val onClick: () -> Unit
)