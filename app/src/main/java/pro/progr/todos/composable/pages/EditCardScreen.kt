package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    val screenScope = rememberCoroutineScope()
    DisposableEffect(cardViewModel, cardId) {
        onDispose { cardViewModel.flushPendingTemplateEdits() }
    }

    LaunchedEffect(cardId) {
        // Wait until both the note and the list collection have loaded. The old code
        // read these during initial composition, when the selected list could still
        // be the default list, and then wrote that value back to the note.
        snapshotFlow { cardViewModel.getCardId() }.first { it == cardId }
        val lists = snapshotFlow { listsViewModel.nestedListState.value }
            .first { it.isNotEmpty() }
        val selectedSublistChain = cardViewModel.getSublistChane()
        val currentList = lists.filterIsInstance<NList>()
            .firstOrNull { it.sublistChain == selectedSublistChain }
        if (currentList == null) {
            // Do not reinterpret a stale selection from another screen as a user edit.
            listsViewModel.selectedListState.value = null
            return@LaunchedEffect
        }
        listsViewModel.selectedListState.value = currentList

        // Initializing the picker is read-only. Persist only an actual selection change.
        var persistedSublistChain = selectedSublistChain
        snapshotFlow { listsViewModel.selectedListState.value as? NList }
            .filterNotNull()
            .collect { selectedList ->
                val newSublistChain = selectedList.sublistChain
                if (newSublistChain != persistedSublistChain) {
                    listsViewModel.bindListToEntity(newSublistChain.toString(), cardId)
                    cardViewModel.updateList(selectedList)
                    persistedSublistChain = newSublistChain
                }
            }
    }

    val cardAction = cardViewModel.cardActionClicked.collectAsState(initial = null)

    cardAction.value?.let { action ->
        cardViewModel.onCardActionClickFired()
        when (action) {
            pro.progr.todos.brightcards.model.CardAction.EDIT_SCHEDULE -> screenScope.launch {
                cardViewModel.flushAndAwaitTemplateEdits()
                navController.navigate("taskSchedule/${cardId}")
            }
            pro.progr.todos.brightcards.model.CardAction.SELECT_LIST -> listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
            pro.progr.todos.brightcards.model.CardAction.SELECT_TAGS -> tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
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
                        CardEditorWithColorPicker(
                            cardViewModel,
                            paletteViewModelState.value,
                            controlsAtTop = true
                        )
                    }
                }
            }
        )

    }
}
