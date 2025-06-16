package pro.progr.brightcards.composable.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import pro.progr.doflow.R
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun TransparentTodoTitle(viewModel: ListedCardViewModel) {
    Box {
        Text(
            modifier = Modifier
                .background(
                    Color.Transparent, shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                ) // Фон заголовка
                .padding(20.dp, top = 55.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            text = viewModel.card.value.title,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Start // Выравнивание текста по центру
        )

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.TopStart)
        ) {
            TodoIcon(viewModel = viewModel)

            IconButton(onClick = { viewModel.menuOn.value = !viewModel.menuOn.value }, modifier = Modifier
                .size(32.dp)
                .padding(vertical = 5.dp, horizontal = 0.dp)) {

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "more",
                    tint = Color(viewModel.card.value.style.textColor())
                )

            }

            DropdownMenu(
                expanded = viewModel.menuOn.value,
                onDismissRequest = { viewModel.menuOn.value = false },
            ) {
                viewModel.card.value.menuItems.forEach { option ->
                    DropdownMenuItem(onClick = {
                        viewModel.menuOn.value = false

                        option.onClick()
                    }) {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = option.text)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier
            .wrapContentWidth()
            .align(Alignment.TopEnd)
            .padding(end = 5.dp, top = 1.dp)) {
            Text(text = viewModel.card.value.reward.toString(),
                modifier = Modifier
                .height(24.dp)
                .padding(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_diamond),
                contentDescription = "Todo",
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}