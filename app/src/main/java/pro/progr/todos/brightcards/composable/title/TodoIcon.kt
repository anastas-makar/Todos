package pro.progr.brightcards.composable.title

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pro.progr.brightcards.composable.visual.NotActiveTodoSquare
import pro.progr.brightcards.composable.visual.NotDoneIcon
import pro.progr.brightcards.composable.visual.TodoSquare
import pro.progr.brightcards.model.TodoStatus
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun TodoIcon(viewModel: ListedCardViewModel) {
    when (viewModel.card.value.todo) {
        TodoStatus.TODO -> TodoSquare(viewModel = viewModel)
        TodoStatus.TODO_NOT_ACTIVE -> NotActiveTodoSquare(viewModel = viewModel)
        TodoStatus.DONE -> Box(modifier = Modifier.padding(top = 5.dp, start = 5.dp)) {
            DoneIcon(viewModel = viewModel)
        }
        TodoStatus.FAIL -> NotDoneIcon(viewModel = viewModel)
        else -> Unit
    }
}