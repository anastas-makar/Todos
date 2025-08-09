package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun ColumnScope.Text(viewModel: ListedCardViewModel, style: CardStyle) {
    Column(
        modifier = Modifier
            .background(
                style.descriptionBackgroundColor,
                shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
            )
            .padding(start = 20.dp, top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.Start)) {
        Text(
            text = viewModel.card.value.text,
            color = style.descriptionTextColor,
            textAlign = TextAlign.Start
        )

        Tags(viewModel = viewModel)

    }
}