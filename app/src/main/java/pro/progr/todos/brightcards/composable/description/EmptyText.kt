package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardRow

@Composable
fun EmptyText(row: CardRow) {
    Box(
        modifier = Modifier
            .background(
                if (row.content.value.fillTitleBackground)
                    Color(row.content.value.style.backgroundColor())
                else
                    Color.Transparent,
                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            ) // Фон заголовка
            .padding(8.dp, bottom = 50.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    )
}