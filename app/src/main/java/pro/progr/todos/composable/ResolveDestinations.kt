package pro.progr.todos.composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.brightcards.vm.Destinations
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import java.time.LocalDate

@Composable
fun ResolveDestinations(
    cardViewModel: ListedCardViewModel,
    diamondViewModel: DiamondViewModel,
    navHostController: NavHostController,
    date: LocalDate
) {
    if (cardViewModel.destination.value == Destinations.DO_TODAY) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.doToday()
    }

    if (cardViewModel.destination.value == Destinations.DO_TOMORROW) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.doTomorrow()
    }

    if (cardViewModel.destination.value == Destinations.MARK_DONE) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.setDone()

        diamondViewModel.updateCount(
            cardViewModel.card.value.reward,
            LocalDate.now())
    }

    if (cardViewModel.destination.value == Destinations.MARK_NOT_DONE) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.setNotDone()

        diamondViewModel.updateCount(
            -cardViewModel.card.value.reward,
            LocalDate.now())
    }

    if (cardViewModel.destination.value == Destinations.REMOVE_FOR_DAY) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.removeForDay(date)
    }

    if (cardViewModel.destination.value == Destinations.TO_EDIT_CARD) {
        cardViewModel.destination.value = Destinations.NONE
        navHostController.navigate("editCard/${cardViewModel.card.value.id}")
    }

    if (cardViewModel.destination.value == Destinations.TO_EDIT_NOTE_IN_HISTORY) {
        cardViewModel.destination.value = Destinations.NONE
        navHostController.navigate("editNoteInHistory/${cardViewModel.card.value.id}/date/${date.toEpochDay()}") //todo:
    }

    if (cardViewModel.destination.value == Destinations.MARK_NOTE_IN_HISTORY_DONE) {
        cardViewModel.destination.value = Destinations.NONE
        cardViewModel.setNoteInHistoryDone(date)

        diamondViewModel.updateCount(
            cardViewModel.card.value.reward,
            LocalDate.now())
    }
}