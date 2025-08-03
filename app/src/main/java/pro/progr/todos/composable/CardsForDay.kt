package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.brightcards.composable.BrightCard
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
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
                cardVm.setMenu(date)

                ResolveDestinations(
                    cardViewModel = cardVm,
                    navHostController = navController,
                    date = date,
                    diamondViewModel = diamondViewModel
                )

                if (cardVm.tagState.value != null) {
                    cardVm.tagState.value?.let { cardTag ->
                        cardsListViewModel.updateTag(NoteTag(cardTag.id, cardTag.title))
                    }

                }

                BrightCard(
                    viewModel = cardVm,
                    onclick = { /** navController.navigate("editCard/${cardVm.card.value.id}") */ }
                )
            }
        }

    }
}