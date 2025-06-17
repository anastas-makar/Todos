package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.composable.DiamondSelector
import pro.progr.todos.brightcards.model.StringAnnotation
import pro.progr.todos.brightcards.vm.CardViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnnotatedClickableText(cardViewModel: CardViewModel) {
    val annotations = cardViewModel.getCardContent().value.stringAnnotations
    FlowRow(
        modifier = Modifier.padding(horizontal = 11.dp),
    ) {

        if (cardViewModel.showReward()) {
            DiamondSelector(cardViewModel)
        }

        annotations.forEach { annotation ->
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
                        // Обработка нажатия на "кнопку"
                        cardViewModel.onAnnotationClicked(StringAnnotation(tag = annotation.tag, text = annotation.text))
                    }
                    .padding(4.dp) // Внутренний отступ текста внутри "кнопки"
            ) {
                Text(
                    text = annotation.text,
                    color = textColor
                )
            }
        }
    }
}