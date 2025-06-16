package pro.progr.doflow.composable.calendar

import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.doflow.CardsListViewModel
import pro.progr.doflow.composable.CardsForDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Future(cardsListViewModel: CardsListViewModel,
           navController: NavHostController,
           date: LocalDate,
           onClick: () -> Unit = {}) {

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
            .clickable { onClick() }
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
            navController,
            date
        )
    }
}