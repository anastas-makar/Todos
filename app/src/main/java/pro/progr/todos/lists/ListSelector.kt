package pro.progr.lists

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable

@Composable
fun ListSelector(viewModel: ListsViewModel) {

    TextButton(
        onClick = {
                  if (viewModel.sheetMode.value == ListsViewModel.SheetMode.HIDDEN) {
                      viewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
                  } else if (viewModel.sheetMode.value == ListsViewModel.SheetMode.SHOWN) {
                      viewModel.sheetMode.value = ListsViewModel.SheetMode.HIDE
                  }
        },
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = viewModel.selectedListState.value?.getName() ?: "")

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Выберите список")

    }
}