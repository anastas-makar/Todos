package pro.progr.todos.composable

import androidx.compose.ui.graphics.Color

data class SmallActionModel(
    val color : Color,
    val text : String,
    val destination: String, //todo: на enum все destination перевести
)