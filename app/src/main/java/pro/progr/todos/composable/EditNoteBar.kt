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
import pro.progr.todos.CardViewModel

@Composable
fun EditNoteBar(navController : NavHostController, viewModel: CardViewModel) {
    TopAppBar(
        title = { Text(text = "Редактировать заметку") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
            }
        },
        actions = {

            IconButton(onClick = {
                viewModel.deleteCard()
                navController.popBackStack()
            }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }

        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}