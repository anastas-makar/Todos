package pro.progr.lists

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.BackButton(viewModel: ListsViewModel) {

    TextButton(
        onClick = { viewModel.modeState.value = ListsViewModel.Mode.SELECT },
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onSurface
        ),
        modifier = Modifier.align(Alignment.CenterStart)
    ) {

        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Добавить список")

    }
}