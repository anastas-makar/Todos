package pro.progr.todos.brightcards.composable.title

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun ColumnScope.EditTitleTransparent(cardViewModel: CardViewModel) {
    TextField(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.CenterHorizontally),
        placeholder = { Text(text = "Заголовок") },
        value = cardViewModel.getCardContent().value.title,
        textStyle = MaterialTheme.typography.h6,
        onValueChange = { v : String -> cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(title = v))},
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent)
    )
}