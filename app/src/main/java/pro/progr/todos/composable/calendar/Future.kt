package pro.progr.todos.composable.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.composable.CardsForDay
import pro.progr.todos.composable.dashedBorder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Future(cardsListViewModel: CardsListViewModel,
           diamondViewModel: DiamondViewModel,
           navController: NavHostController,
           date: LocalDate,
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

        Row(modifier = Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .align(Alignment.CenterHorizontally)) {
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.body1
            )

        }

        CardsForDay(
            cardsListViewModel,
            diamondViewModel,
            navController,
            date
        )
    }
}