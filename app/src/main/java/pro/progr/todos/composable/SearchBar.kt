package pro.progr.doflow.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.progr.doflow.CardsListViewModel

@Composable
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun BoxScope.SearchBar(cardsListViewModel: CardsListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(0.dp)
            .align(Alignment.BottomCenter)
            .background(Color.White)
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
            .imePadding(),
    ) {
        Box(modifier = Modifier
            .height(20.dp)
            .fillMaxWidth()
        )

        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                cardsListViewModel.searchNotes(it)
                            },
            placeholder = { Text("Искать...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp, start = 20.dp, end = 20.dp)
                .wrapContentHeight(),
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Normal) // измените размер шрифта здесь
        )
    }
}
