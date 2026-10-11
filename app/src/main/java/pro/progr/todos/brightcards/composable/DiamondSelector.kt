package pro.progr.todos.brightcards.composable

import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pro.progr.todos.R
import pro.progr.todos.brightcards.composable.visual.SquareButton
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun DiamondSelector(cardViewModel: CardViewModel) {
    val expanded = remember { mutableStateOf(false) }
    val noteLabel = stringResource(R.string.note)
    val icon =
        painterResource(id = R.drawable.ic_diamond)
    val options = listOf(100, 50, 25, 15, 10, 5, 1, noteLabel)


    SquareButton(onClick = { expanded.value = true }, cardViewModel = cardViewModel) {

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {


                    // Обработка выбора
                    cardViewModel.updateCard(
                        cardViewModel.getCardContent().value.copy(
                            todo = if (option != noteLabel) TodoStatus.TODO else TodoStatus.NOT_ACTIVE,
                            reward = if (option != noteLabel) (option as Int) else cardViewModel.getCardContent().value.reward
                        )
                    )

                    expanded.value = false
                }) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = option.toString())
                        if (option != noteLabel) {
                            Icon(
                                painter = icon,
                                contentDescription = stringResource(R.string.diamond),
                                modifier = Modifier.size(24.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}
