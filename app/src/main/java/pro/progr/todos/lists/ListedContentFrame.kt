package pro.progr.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ListedContentFrame(listsViewModel: ListsViewModel, content: @Composable () -> Unit) {

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    if (listsViewModel.sheetMode.value == ListsViewModel.SheetMode.SHOW) {
        coroutineScope.launch {
            sheetState.show()
            listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOWN
        }
    } else if (listsViewModel.sheetMode.value == ListsViewModel.SheetMode.HIDE) {
        coroutineScope.launch {
            sheetState.hide()
        }
        listsViewModel.sheetMode.value = ListsViewModel.SheetMode.HIDDEN
    }

    if (listsViewModel.sheetMode.value == ListsViewModel.SheetMode.SHOWN  && !sheetState.isVisible) {
        listsViewModel.sheetMode.value = ListsViewModel.SheetMode.HIDDEN
    }

    if (sheetState.isVisible && listsViewModel.editedListIdState.value == null) {
        listsViewModel.modeState.value = ListsViewModel.Mode.SELECT
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.32f),  // Установка scrim цвета
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = if (sheetState.isVisible) 5.dp else 0.dp,
        sheetGesturesEnabled = true,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(2.dp)
            ) {
                Divider(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .padding(top = 2.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )

                when(listsViewModel.modeState.value) {
                    ListsViewModel.Mode.SELECT -> SelectListScreen(listsViewModel = listsViewModel)
                    ListsViewModel.Mode.ADD -> AddListScreen(listsViewModel = listsViewModel)
                    ListsViewModel.Mode.EDIT -> EditListsScreen(listsViewModel = listsViewModel)
                }
            }
        },
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {
        content()
    }
}
