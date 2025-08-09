package pro.progr.todos.brightcards.composable.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.R
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun Title(viewModel: ListedCardViewModel,
          diamondViewModel: DiamondViewModel,
          style: CardStyle) {
    Box {
        Text(
            modifier = Modifier
                .background(
                    style.titleBackgroundColor,
                    shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                ) // Фон заголовка
                .padding(20.dp, bottom = 10.dp, top = 45.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            text = viewModel.card.value.title,
            style = MaterialTheme.typography.h6,
            color = style.titleTextColor,
            textAlign = TextAlign.Start // Выравнивание текста по центру
        )

        if (viewModel.card.value.todo == TodoStatus.NOT_ACTIVE) {
            MenuBox(viewModel = viewModel, style = style)
        } else {
            TodoRow(viewModel, diamondViewModel, style)
        }

        Row(modifier = Modifier
            .wrapContentWidth()
            .align(Alignment.TopEnd)
            .padding(end = 5.dp, top = 1.dp)) {
            Text(text = viewModel.card.value.reward.toString(), color = style.titleTextColor,
                modifier = Modifier
                    .height(24.dp)
                    .padding(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_diamond),
                contentDescription = "Todo",
                tint = style.titleTextColor,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }
    }


}