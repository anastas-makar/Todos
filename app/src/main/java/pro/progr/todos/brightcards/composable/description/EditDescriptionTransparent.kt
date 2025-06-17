package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun ColumnScope.EditDescriptionTransparent(cardViewModel: CardViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .align(Alignment.CenterHorizontally)
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .fillMaxHeight(),
            value = cardViewModel.getCardContent().value.text,
            onValueChange = { v: String ->
                cardViewModel.updateCard(
                    cardViewModel.getCardContent().value.copy(
                        text = v
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Описание") }
        )

        //if (cardViewModel.getCardContent().value.text.isNotEmpty() || cardViewModel.getCardContent().value.title.isNotEmpty()) {
            AnnotatedClickableText(cardViewModel)
        //}
    }
}