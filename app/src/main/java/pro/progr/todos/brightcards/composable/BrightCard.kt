package pro.progr.todos.brightcards.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.composable.description.Description
import pro.progr.todos.brightcards.composable.title.Title
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.CardTag
import pro.progr.todos.CardRow

@Composable
fun BrightCard(
    row: CardRow,
    menuItems: List<MenuItem>,
    onDoneClick: () -> Unit,
    onclick: () -> Unit = {},
    onTagClick: (CardTag) -> Unit
) {
    val style = remember(row.content) { CardStyle(row.content) }
    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                1.dp,
                Color(row.content.value.style.backgroundColor()),
                RoundedCornerShape(5.dp)
            ) // Добавление границы
            .clickable(onClick = onclick)
    ) {


        Title(row = row, menuItems = menuItems, onDoneClick = onDoneClick, style = style)

        Description(row = row, style = style, onTagClick = onTagClick)

    }
}
