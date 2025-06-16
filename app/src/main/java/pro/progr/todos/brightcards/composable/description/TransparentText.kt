package pro.progr.brightcards.composable.description

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun ColumnScope.TransparentText(viewModel: ListedCardViewModel) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.Start)) {
        Text(
            text = viewModel.card.value.text,
            textAlign = TextAlign.Start
        )

        Tags(viewModel = viewModel)
    }
}