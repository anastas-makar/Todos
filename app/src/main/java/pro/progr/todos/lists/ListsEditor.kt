package pro.progr.lists

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListsEditor(listsViewModel: ListsViewModel) {

    LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 0.dp, start = 20.dp, end = 20.dp)) {
        items(listsViewModel.nestedListState.value.size) { index ->
            val nestedList = listsViewModel.nestedListState.value[index]
            EditListItem(viewModel = listsViewModel, nestedList = nestedList)
        }
    }

    if (listsViewModel.editedListIdState.value != null) {
        val nestedList = listsViewModel.nestedListState.value.find {
            it.getId() == listsViewModel.editedListIdState.value }
        nestedList?.let {
            EditListDialog(viewModel = listsViewModel,
                nestedList = nestedList,
                parentList = listsViewModel.findParent(nestedList))

        }
    }
}
