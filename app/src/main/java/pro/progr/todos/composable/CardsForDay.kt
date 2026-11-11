package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
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
fun CardsForDay(
    cardsListViewModel: CardsListViewModel,
    diamondViewModel: DiamondViewModel,
    navController: NavHostController,
    date: LocalDate
) {

    val currentDate = LocalDate.now()
    if (date.isAfter(currentDate)) {
        val activeCards = cardsListViewModel.getCardsForDate(date).collectAsState()
        Column {
            activeCards.value.forEach { cardVm ->
                key(cardVm.key) {
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
