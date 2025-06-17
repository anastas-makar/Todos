package pro.progr.todos.brightcards.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.colors.ColorStyle

@Composable
fun ColorItem(color: ColorStyle, selectedColor: ColorStyle, onColorSelected: (ColorStyle) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(0.dp)
            .background(Color(color.backgroundColor()), RoundedCornerShape(5.dp))
            .clickable { onColorSelected(color) }
            .border(
                if (selectedColor.backgroundColor().equals(color.backgroundColor())) 1.dp else 0.dp,
                Color.Black,
                RoundedCornerShape(5.dp)
            )
            .padding(8.dp),
    )
}