package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pro.progr.todos.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.EditCardInHistoryViewModel
import pro.progr.todos.composable.EditNoteInHistoryBar
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditNoteInHistoryScreen(cardViewModel: EditCardInHistoryViewModel,
                            listsViewModel: ListsViewModel,
                            epochDay: Long,
                            paletteViewModelState: MutableState<PaletteViewModel>,
                            navController: NavHostController
) {
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                EditNoteInHistoryBar(
                    navController = navController,
                    viewModel = cardViewModel,
                    date = LocalDate.ofEpochDay(epochDay)
                )
            }
        },
        content = { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .navigationBarsPadding()
            ) {
                ListedContentFrame(listsViewModel = listsViewModel) {
                    CardEditorWithColorPicker(
                        cardViewModel,
                        paletteViewModelState.value
                    )
                }
            }
        }
    )
}