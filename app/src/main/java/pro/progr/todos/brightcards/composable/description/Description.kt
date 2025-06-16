package pro.progr.brightcards.composable.description

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun ColumnScope.Description(viewModel: ListedCardViewModel) {
    when {
        viewModel.card.value.text.isEmpty()
                && viewModel.card.value.tags.isEmpty() -> EmptyText(viewModel = viewModel)
        viewModel.card.value.fillTextBackground -> FilledText(viewModel = viewModel)
        !viewModel.card.value.fillTextBackground -> TransparentText(viewModel = viewModel)
    }
}