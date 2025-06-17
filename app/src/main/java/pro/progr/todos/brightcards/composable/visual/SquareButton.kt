package pro.progr.todos.brightcards.composable.visual

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pro.progr.todos.R
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun SquareButton(
    onClick: () -> Unit,
    cardViewModel: CardViewModel,
    dropdownContent: @Composable () -> Unit = {}
) {
    val borderColor = if (cardViewModel.getCardContent().value.fillTextBackground)
        Color(cardViewModel.getCardContent().value.style.secondaryElementsColor())
    else
        MaterialTheme.colors.onSurface


    val textColor = if (cardViewModel.getCardContent().value.fillTextBackground)
        Color(cardViewModel.getCardContent().value.style.textColor())
    else
        MaterialTheme.colors.onSurface

    // Используйте Box для создания фона и отступов вокруг текста

    Box(
        modifier = Modifier
            .padding(4.dp) // Отступ между "кнопками"
            //.background(Color.Transparent, shape = RoundedCornerShape(4.dp)) // Фон и скругление углов
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable {
                onClick()
            }
            .padding(4.dp) // Внутренний отступ текста внутри "кнопки"
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if (cardViewModel.getCardContent().value.todo == TodoStatus.NOT_ACTIVE)
                Text(text = "Заметка", color = textColor)
            else {
                Text(
                    text = cardViewModel.getCardContent().value.reward.toString(),
                    color = textColor,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_diamond),
                    contentDescription = "Diamond",
                    modifier = Modifier
                        .height(10.dp)
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically),
                    tint = textColor
                )
            }
        }
    }

    dropdownContent()
}