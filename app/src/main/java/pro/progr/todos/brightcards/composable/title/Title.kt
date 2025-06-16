package pro.progr.brightcards.composable.title

import androidx.compose.runtime.Composable
import pro.progr.brightcards.model.TodoStatus
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun Title(viewModel: ListedCardViewModel) {
    when {
        viewModel.card.value.todo == TodoStatus.NOT_ACTIVE && viewModel.card.value.title.isEmpty() -> EmptyTitle(viewModel = viewModel)
        viewModel.card.value.todo != TodoStatus.NOT_ACTIVE && viewModel.card.value.fillTitleBackground -> FilledTodoTitle(viewModel = viewModel)
        viewModel.card.value.todo != TodoStatus.NOT_ACTIVE && !viewModel.card.value.fillTitleBackground -> TransparentTodoTitle(viewModel = viewModel)
        viewModel.card.value.todo == TodoStatus.NOT_ACTIVE && viewModel.card.value.fillTitleBackground -> FilledTitle(viewModel = viewModel)
        viewModel.card.value.todo == TodoStatus.NOT_ACTIVE && !viewModel.card.value.fillTitleBackground -> TransparentTitle(viewModel = viewModel)
    }

}