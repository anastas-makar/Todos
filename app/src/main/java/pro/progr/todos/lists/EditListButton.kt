package pro.progr.lists

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.EditListButton(viewModel: ListsViewModel) {

    TextButton(
        onClick = { viewModel.modeState.value = ListsViewModel.Mode.EDIT },
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onSurface
        ),
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        Text(text = stringResource(R.string.edit))

        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = stringResource(R.string.edit_lists))

    }
}