package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.model.CardTag
import pro.progr.todos.CardRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(row: CardRow, onTagClick: (CardTag) -> Unit) {


    val textColor = if (row.content.value.fillTextBackground)
        Color(row.content.value.style.textColor())
    else
        MaterialTheme.colors.onSurface

    FlowRow(
        modifier = Modifier
            .padding(start = 0.dp, top = 20.dp)
    ) {
        row.content.value.tags.forEach { tag ->

            // Используйте Box для создания фона и отступов вокруг текста
            Box(
                modifier = Modifier
                    .padding(start = 0.dp, end = 4.dp) // Отступ между "кнопками"
                    //.background(Color.Transparent, shape = RoundedCornerShape(4.dp)) // Фон и скругление углов
                    .clickable {
                        onTagClick(tag)

                    }
                    .padding(start = 0.dp) // Внутренний отступ текста внутри "кнопки"
            ) {
                Text(
                    text = "#$tag",
                    color = textColor
                )
            }
        }

    }
}
