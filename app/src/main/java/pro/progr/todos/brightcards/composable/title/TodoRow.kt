package pro.progr.todos.brightcards.composable.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun BoxScope.TodoRow(viewModel: ListedCardViewModel,
                     diamondViewModel: DiamondViewModel,
                     style: CardStyle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(style.titleBackgroundColor)
            .align(Alignment.TopStart)
    ) {
        TodoIcon(viewModel = viewModel, diamondViewModel = diamondViewModel)

        IconButton(onClick = { viewModel.menuOn.value = !viewModel.menuOn.value }, modifier = Modifier
            .size(32.dp)
            .padding(vertical = 5.dp, horizontal = 0.dp)) {

            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "more",
                tint = style.titleTextColor
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
}