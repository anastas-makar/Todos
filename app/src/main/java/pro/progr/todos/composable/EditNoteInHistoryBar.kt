package pro.progr.todos.composable

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pro.progr.todos.EditCardInHistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EditNoteInHistoryBar(navController : NavHostController, viewModel: EditCardInHistoryViewModel, date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyy", Locale.getDefault())

    TopAppBar(
        title = { Text(text = "Редактировать для ${date.format(formatter)}") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
            }
        },
        actions = {

            IconButton(onClick = {
                viewModel.removeForDay(date)
                navController.popBackStack()
            }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }

        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}