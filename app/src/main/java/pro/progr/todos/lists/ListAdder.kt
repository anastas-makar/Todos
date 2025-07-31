package pro.progr.lists

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListAdder(listsViewModel: ListsViewModel) {

    LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 0.dp, start = 20.dp, end = 20.dp)) {
        itemsIndexed(listsViewModel.nestedListState.value) { _, nestedList ->
            ListAdderElement(viewModel = listsViewModel, nestedList = nestedList)
        }
    }

    if (listsViewModel.editedListIdState.value != null) {
        val nestedList = listsViewModel.nestedListState.value.find {
            it.id == listsViewModel.editedListIdState.value }
        nestedList?.let {
            EditListDialog(viewModel = listsViewModel,
                nestedList = nestedList,
                parentList = listsViewModel.findParent(nestedList))

        }
    }
}
