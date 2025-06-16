package pro.progr.lists

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun EditListsScreen(listsViewModel: ListsViewModel) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        BackButton(viewModel = listsViewModel)
    }

    ListsEditor(listsViewModel)
}
