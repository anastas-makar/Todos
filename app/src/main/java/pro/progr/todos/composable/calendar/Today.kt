package pro.progr.todos.composable.calendar

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.brightcards.composable.BrightCard
import pro.progr.todos.CardRow
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.brightcards.vm.Destinations
import pro.progr.todos.composable.dashedBorder
import pro.progr.todos.db.NoteTag
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Today(
    cardsListViewModel: CardsListViewModel,
    diamondViewModel : DiamondViewModel,
    navController: NavHostController,
    date: LocalDate,
    cardsForDate:  List<CardRow>,
    onClick: () -> Unit = {}) {

    val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyy", Locale.getDefault())

    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .dashedBorder(
                width = 1.dp,
                color = Color.LightGray,
                shape = shape,
                dashLength = 6.dp,
                gapLength = 4.dp
            )
            .clip(shape) // чтобы ripple/клик тоже был со скруглением
            .clickable { onClick() }
            .padding(bottom = 10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.body1
            )
            Text(
                text = " ◆ ${diamondViewModel.diamondsCountForDates.value[date.toEpochDay()] ?: 0} ",
                style = MaterialTheme.typography.body1
            )
            Icon(
                painter = painterResource(id = pro.progr.todos.R.drawable.ic_diamond_bright),
                contentDescription = stringResource(R.string.diamond),
                tint = Color.Unspecified,
                modifier = Modifier.height(12.dp)
            )
        }

        Column {
            cardsForDate.forEach { cardVm ->
                key(cardVm.key) {
                /** navController.navigate("editCard/${cardVm.content.value.id}") */
                /** navController.navigate("editCard/${cardVm.content.value.id}") */

                val handleAction: (Destinations) -> Unit = { action ->
                    cardsListViewModel.handleCardAction(
                        card = cardVm,
                        action = action,
                        date = date,
                        navigate = { route -> navController.navigate(route) },
                        updateReward = { amount, rewardDate -> diamondViewModel.updateCount(amount, rewardDate) }
                    )
                }

                BrightCard(
                    row = cardVm,
                    menuItems = cardsListViewModel.menuItems(cardVm, date, handleAction),
                    onDoneClick = { handleAction(Destinations.MARK_DONE) },
                    onTagClick = { cardTag ->
                        cardsListViewModel.updateTag(NoteTag(cardTag.id, cardTag.title))
                    },
                    onclick = { /** navController.navigate("editCard/${cardVm.content.value.id}") */ }
                )
                }
            }
        }
    }
}
