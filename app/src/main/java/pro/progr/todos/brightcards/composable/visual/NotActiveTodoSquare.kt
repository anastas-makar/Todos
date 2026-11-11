package pro.progr.todos.brightcards.composable.visual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardRow

@Composable
fun NotActiveTodoSquare(row: CardRow) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(5.dp)
            .border(
                1.dp,
                if (row.content.value.fillTitleBackground) Color(row.content.value.style.secondaryElementsColor())
                else MaterialTheme.colors.onSurface, shape = RoundedCornerShape(4.dp)
            )
            .background(Color(0x44ffffff), shape = RoundedCornerShape(4.dp))
    )
}