package pro.progr.todos.brightcards.composable.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pro.progr.todos.R
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun TransparentTitle(viewModel: ListedCardViewModel) {
    Box {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 5.dp)
        ) {
            IconButton(onClick = { viewModel.menuOn.value = !viewModel.menuOn.value },
                modifier = Modifier.size(32.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "more",
                    tint = MaterialTheme.colors.onSurface
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
        Text(
            modifier = Modifier
                .background(
                    Color.Transparent, shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                ) // Фон заголовка
                .padding(20.dp, top = 40.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            text = viewModel.card.value.title,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Start // Выравнивание текста по центру
        )

    }
}