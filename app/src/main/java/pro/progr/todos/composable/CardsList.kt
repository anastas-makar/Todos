package pro.progr.todos.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
        itemsIndexed(cardsListViewModel.cardsList.value) { index, cardVm ->
            cardVm.setMenu()

            ResolveDestinations(
                cardViewModel = cardVm,
                navHostController = navController,
                date = LocalDate.now(),
                diamondViewModel = diamondViewModel
            )

            if (cardVm.tagState.value != null) {
                cardVm.tagState.value?.let { cardTag ->
                    cardsListViewModel.updateTag(NoteTag(cardTag.id, cardTag.title))
                }

            }

            BrightCard(
                cardVm,
                diamondViewModel = diamondViewModel,
                onclick = {
                    //navController.navigate("editCard/${cardVm.card.value.id}")
                })

        }
    }
}