package pro.progr.todos.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardsListViewModel

@Composable
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun BottomSearch(cardsListViewModel: CardsListViewModel) {

    Box {

        SearchBar(cardsListViewModel)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(2.dp)
                .align(Alignment.BottomCenter)
        )

    }
}