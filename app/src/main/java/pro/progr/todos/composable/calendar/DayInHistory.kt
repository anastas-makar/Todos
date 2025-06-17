package pro.progr.todos.composable.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.R
import pro.progr.todos.brightcards.composable.BrightCard
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.composable.ResolveDestinations
import pro.progr.todos.db.NoteTag
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun DayInHistory(
    cardsListViewModel: CardsListViewModel,
    diamondViewModel : DiamondViewModel,
    navController: NavHostController,
    date: LocalDate,
    cardsForDate:  List<ListedCardViewModel>) {

    val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .padding(bottom = 10.dp)
    ) {
        val count = diamondViewModel.diamondsCountForDates.value[date.toEpochDay()]

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .align(Alignment.CenterHorizontally)) {
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.body1
            )
            if (count != null) {
                Text(text = " ◆ $count ",
                    style = MaterialTheme.typography.body1)

                Icon(
                    painter = painterResource(id = R.drawable.ic_diamond),
                    contentDescription = "Diamond",
                    modifier = Modifier
                        .size(12.dp)
                )
            }

        }

        Column {
            cardsForDate.forEach { cardVm ->
                cardVm.setMenu(date)

                if (cardVm.tagState.value != null) {
                    cardVm.tagState.value?.let { cardTag ->
                        cardsListViewModel.updateTag(NoteTag(cardTag.id, cardTag.title))
                    }

                }

                ResolveDestinations(
                    cardViewModel = cardVm,
                    navHostController = navController,
                    date = date
                )

                BrightCard(
                    viewModel = cardVm,
                    onclick = { /** navController.navigate("editCard/${cardVm.card.value.id}") */ }
                )
            }
        }
    }
}