package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ColumnScope.EditDescriptionFilled(cardViewModel: CardViewModel) {
    Column(
        modifier = Modifier
            .background(
                Color(cardViewModel.getCardContent().value.style.backgroundColor()),
                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .align(Alignment.CenterHorizontally)
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            value = cardViewModel.getCardContent().value.text,
            onValueChange = { v: String ->
                cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(text = v))
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(cardViewModel.getCardContent().value.style.textColor()),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Описание", color = Color(cardViewModel.getCardContent().value.style.placeHolderColor())) }
        )

        //if (cardViewModel.getCardContent().value.text.isNotEmpty() || cardViewModel.getCardContent().value.title.isNotEmpty()) {
            AnnotatedClickableText(cardViewModel)
        //}
    }
}