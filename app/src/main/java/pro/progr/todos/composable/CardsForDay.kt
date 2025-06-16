package pro.progr.doflow.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.brightcards.composable.BrightCard
import pro.progr.doflow.CardsListViewModel
import pro.progr.doflow.db.NoteTag
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CardsForDay(
    cardsListViewModel: CardsListViewModel,
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
                    date = date
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