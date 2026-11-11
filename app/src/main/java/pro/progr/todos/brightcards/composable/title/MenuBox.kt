package pro.progr.todos.brightcards.composable.title

import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pro.progr.todos.R
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MenuBox(viewModel: ListedCardViewModel,
            menuItems: List<MenuItem>,
            style: CardStyle
) {
    var menuOn by remember(viewModel.rowKey) { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(
                style.titleBackgroundColor,
                shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
            )
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        IconButton(onClick = { menuOn = !menuOn }, modifier = Modifier
            .size(40.dp)
            .border(width = 0.dp, color = Color.Transparent)
            .padding(bottom = 15.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
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
