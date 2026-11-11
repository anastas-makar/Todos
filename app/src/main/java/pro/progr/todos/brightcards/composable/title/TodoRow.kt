package pro.progr.todos.brightcards.composable.title

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun BoxScope.TodoRow(viewModel: ListedCardViewModel,
                     menuItems: List<MenuItem>,
                     onDoneClick: () -> Unit,
                     style: CardStyle
) {
    var menuOn by remember(viewModel.rowKey) { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(style.titleBackgroundColor,
                shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
            .align(Alignment.TopStart)
    ) {
        TodoIcon(viewModel = viewModel, onDoneClick = onDoneClick)

        IconButton(onClick = { menuOn = !menuOn }, modifier = Modifier
            .size(32.dp)
            .padding(vertical = 5.dp, horizontal = 0.dp)) {

            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more),
                tint = style.titleTextColor
            )

        }

        DropdownMenu(
            expanded = menuOn,
            onDismissRequest = { menuOn = false },
        ) {
            menuItems.forEach { option ->
                DropdownMenuItem(onClick = {
                    menuOn = false

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
