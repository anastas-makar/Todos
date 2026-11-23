package pro.progr.todos.brightcards.composable.visual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.progr.todos.CardRow

@Composable
fun NotActiveTodoSquare(row: CardRow) {
    val borderColor = if (row.content.value.fillTitleBackground) {
        Color(row.content.value.style.secondaryElementsColor()).copy(alpha = 0.4f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(5.dp)
            .border(
                1.dp,
                borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(Color(0x22ffffff), shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "~",
            color = borderColor,
            fontSize = 16.sp,
            lineHeight = 16.sp
        )
    }
}
