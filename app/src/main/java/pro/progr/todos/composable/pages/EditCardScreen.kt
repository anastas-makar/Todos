package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pro.progr.todos.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.CardViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.composable.AddTagsScreen
import pro.progr.todos.composable.EditNoteBar
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditCardScreen(cardViewModel: CardViewModel,
                   tagsViewModel: TagsViewModel,
                   listsViewModel: ListsViewModel,
                   cardId: String,
                   paletteViewModelState: MutableState<PaletteViewModel>,
                   navController: NavHostController
) {
    LaunchedEffect(Unit) {
        val selectedSublistChain = cardViewModel.getSublistChane()
        for (list in listsViewModel.nestedListState.value) {
            if ((list as NList).sublistChain.equals(selectedSublistChain)) {
                listsViewModel.selectedListState.value = list
            }
        }

    }

    if (listsViewModel.selectedListState.value != null) {

        cardViewModel.getCardId()?.also { gotCardId ->
            listsViewModel.bindListToEntity(
                (listsViewModel.selectedListState.value as NList).sublistChain.toString(),
                gotCardId
            )
            cardViewModel.updateList(listsViewModel.selectedListState.value as NList)
        }

    }

    val annotation = cardViewModel.annotationClicked.collectAsState(initial = null)

    annotation.value?.let {
        cardViewModel.onAnnotationClickFired()
        if (it.tag == "schedule") {
            navController.navigate("taskSchedule/${cardId}")
        } else if (it.tag == "list") {
            listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
        }  else if (it.tag == "tags") {
            tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
        }
    }
    AddTagsScreen(tagsViewModel, cardViewModel){
        Scaffold(
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    EditNoteBar(navController, cardViewModel)
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
                        CardEditorWithColorPicker(cardViewModel, paletteViewModelState.value)
                    }
                }
            }
        )

    }
}