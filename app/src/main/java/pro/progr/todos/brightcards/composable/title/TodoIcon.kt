package pro.progr.todos.brightcards.composable.title

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.composable.visual.NotActiveTodoSquare
import pro.progr.todos.brightcards.composable.visual.NotDoneIcon
import pro.progr.todos.brightcards.composable.visual.TodoSquare
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.CardRow

@Composable
fun TodoIcon(row: CardRow, onDoneClick: () -> Unit) {
    when (row.content.value.todo) {
        TodoStatus.TODO -> TodoSquare(row = row, onDoneClick = onDoneClick)
        TodoStatus.TODO_NOT_ACTIVE -> NotActiveTodoSquare(row = row)
        TodoStatus.DONE -> Box(modifier = Modifier.padding(top = 5.dp, start = 5.dp)) {
            DoneIcon(row = row)
        }
        TodoStatus.FAIL -> NotDoneIcon(row = row)
        else -> Unit
    }
}
