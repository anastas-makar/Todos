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
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.composable.description.Description
import pro.progr.todos.brightcards.composable.title.Title
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun BrightCard(viewModel: ListedCardViewModel, diamondViewModel: DiamondViewModel, onclick: () -> Unit = {}) {
    val style = remember { CardStyle(viewModel.card) }
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
                Color(viewModel.card.value.style.backgroundColor()),
                RoundedCornerShape(5.dp)
            ) // Добавление границы
            .clickable(onClick = onclick)
    ) {


        Title(viewModel = viewModel,
            diamondViewModel = diamondViewModel,
            style = style)

        Description(viewModel = viewModel)

    }
}