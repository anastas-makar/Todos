package pro.progr.todos.composable

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun TagsBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.tags_and_filters))
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}