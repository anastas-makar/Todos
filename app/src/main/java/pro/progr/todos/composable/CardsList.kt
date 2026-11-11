package pro.progr.todos.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.brightcards.composable.BrightCard
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.brightcards.vm.Destinations
import pro.progr.todos.db.NoteTag
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CardsList(cardsListViewModel: CardsListViewModel,
              diamondViewModel: DiamondViewModel,
              navController: NavHostController) {
    val shape = RoundedCornerShape(10.dp)

    LazyColumn(modifier = Modifier
        .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 60.dp)
        .dashedBorder(
            width = 1.dp,
            color = Color.LightGray,
            shape = shape,
            dashLength = 6.dp,
            gapLength = 4.dp
        )
        .clip(shape) // чтобы ripple/клик тоже был со скруглением
        .padding(vertical = 25.dp)
        .fillMaxHeight()) {
        items(cardsListViewModel.cardsList.value, key = { cardVm -> cardVm.rowKey.viewModelStoreKey() }) { cardVm ->
            val handleAction: (Destinations) -> Unit = { action ->
                cardsListViewModel.handleCardAction(
                    card = cardVm,
                    action = action,
                    date = LocalDate.now(),
                    navigate = { route -> navController.navigate(route) },
                    updateReward = { amount, date -> diamondViewModel.updateCount(amount, date) }
                )
            }

            BrightCard(
                viewModel = cardVm,
                menuItems = cardsListViewModel.menuItems(cardVm, null, handleAction),
                onDoneClick = { handleAction(Destinations.MARK_DONE) },
                onTagClick = { cardTag ->
                    cardsListViewModel.updateTag(NoteTag(cardTag.id, cardTag.title))
                },
                onclick = {
                    //navController.navigate("editCard/${cardVm.card.value.id}")
                })

        }
    }
}
