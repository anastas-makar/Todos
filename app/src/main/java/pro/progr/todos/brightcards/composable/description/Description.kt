package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun ColumnScope.Description(viewModel: ListedCardViewModel, style: CardStyle) {
    when {
        viewModel.card.value.text.isEmpty()
                && viewModel.card.value.tags.isEmpty() -> EmptyText(viewModel = viewModel)
        else -> FilledText(viewModel = viewModel, style = style)
    }
}