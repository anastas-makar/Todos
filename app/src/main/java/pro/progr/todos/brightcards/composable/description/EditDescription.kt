package pro.progr.todos.brightcards.composable.description

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun ColumnScope.EditDescription(cardViewModel: CardViewModel,
                                style: CardStyle,
                                showMetadataActions: Boolean = true) {
    val cardState = cardViewModel.getCardContent()
    val descriptionInput = remember(cardState.value.id) {
        mutableStateOf(TextFieldValue(cardState.value.text))
    }
    Column(
        modifier = Modifier
            .background(
                style.descriptionBackgroundColor,
                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .align(Alignment.CenterHorizontally)
            .padding(10.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            value = descriptionInput.value,
            onValueChange = { value: TextFieldValue ->
                descriptionInput.value = value
                cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(text = value.text))
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = style.descriptionTextColor,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = stringResource(R.string.description), color = style.descriptionPlaceHolderColor) }
        )

        //if (cardViewModel.getCardContent().value.text.isNotEmpty() || cardViewModel.getCardContent().value.title.isNotEmpty()) {
            if (showMetadataActions) CardMetadataActions(cardViewModel)
        //}
    }
}
