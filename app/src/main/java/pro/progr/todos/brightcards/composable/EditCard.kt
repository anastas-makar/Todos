package pro.progr.todos.brightcards.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import pro.progr.todos.brightcards.composable.description.EditDescription
import pro.progr.todos.brightcards.composable.title.EditTitle
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun EditCard(cardViewModel: CardViewModel) {
    val style = remember { CardStyle(cardViewModel.getCardContent()) }

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                1.dp,
                Color(cardViewModel.getCardContent().value.style.backgroundColor()),
                RoundedCornerShape(5.dp)
            ) // Добавление границы
            .fillMaxHeight()
    ) {

        EditTitle(cardViewModel = cardViewModel, style = style)

        EditDescription(cardViewModel = cardViewModel, style = style)

    }
}
