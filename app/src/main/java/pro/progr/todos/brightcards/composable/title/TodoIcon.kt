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
import java.time.LocalDate

@Composable
fun TodoIcon(row: CardRow, onDoneClick: () -> Unit) {
    val todo = row.content.value.todo
    val isFutureOccurrence = row.key.epochDay?.let { it > LocalDate.now().toEpochDay() } == true

    val isFutureTodo = todo == TodoStatus.TODO || todo == TodoStatus.DONE || todo == TodoStatus.TODO_NOT_ACTIVE
    if (isFutureOccurrence && isFutureTodo) {
        NotActiveTodoSquare(row = row)
    } else {
        when (todo) {
            TodoStatus.TODO -> TodoSquare(row = row, onDoneClick = onDoneClick)
            TodoStatus.TODO_NOT_ACTIVE -> NotActiveTodoSquare(row = row)
            TodoStatus.DONE -> Box(modifier = Modifier.padding(top = 5.dp, start = 5.dp)) {
                DoneIcon(row = row)
            }
            TodoStatus.FAIL -> NotDoneIcon(row = row)
            else -> Unit
        }
    }
}
