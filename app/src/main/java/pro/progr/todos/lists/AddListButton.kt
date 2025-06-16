package pro.progr.lists

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.AddListButton(viewModel: ListsViewModel) {

    TextButton(
        onClick = { viewModel.modeState.value = ListsViewModel.Mode.ADD },
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onSurface
        ),
        modifier = Modifier.align(Alignment.CenterStart)
    ) {

        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "Добавить список")

        Text(text = "Добавить")

    }
}