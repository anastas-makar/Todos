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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pro.progr.todos.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.CardViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.composable.AddTagsScreen
import pro.progr.todos.composable.NoteBar
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateCardScreen(cardViewModel: CardViewModel,
                     tagsViewModel: TagsViewModel,
                     listsViewModel: ListsViewModel,
                     paletteViewModelState: MutableState<PaletteViewModel>,
                     navController: NavHostController) {
    val cardAction = cardViewModel.cardActionClicked.collectAsState(initial = null)

    if (listsViewModel.selectedListState.value != null) {

        cardViewModel.getCardId()?.also { gotCardId ->
            listsViewModel.bindListToEntity(
                (listsViewModel.selectedListState.value as NList).sublistChain.toString(),
                gotCardId
            )
            cardViewModel.updateList(listsViewModel.selectedListState.value as NList)
        }

    }

    if (listsViewModel.selectedListState.value != null) {

        cardViewModel.getCardId()?.also { cardId ->
            listsViewModel.bindListToEntity(
                (listsViewModel.selectedListState.value as NList).sublistChain.toString(),
                cardId
            )
        }

    }

    AddTagsScreen(tagsViewModel, cardViewModel) {
        Scaffold(
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    NoteBar(navController, cardViewModel)
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

    cardAction.value?.let { action ->
        cardViewModel.onCardActionClickFired()
        when (action) {
            pro.progr.todos.brightcards.model.CardAction.EDIT_SCHEDULE -> navController.navigate("taskSchedule/${cardViewModel.getCardId()}")
            pro.progr.todos.brightcards.model.CardAction.SELECT_LIST -> listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
            pro.progr.todos.brightcards.model.CardAction.SELECT_TAGS -> tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
        }
    }
}