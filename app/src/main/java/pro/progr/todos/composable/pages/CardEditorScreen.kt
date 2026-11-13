package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.LinkedHashMap
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import pro.progr.todos.CardViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.todos.brightcards.model.CardAction
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.composable.AddTagsScreen
import pro.progr.todos.composable.NoteBar
import pro.progr.todos.datefilters.FilterType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardEditorScreen(
    cardViewModel: CardViewModel,
    tagsViewModel: TagsViewModel,
    listsViewModel: ListsViewModel,
    paletteViewModelState: MutableState<PaletteViewModel>,
    navController: NavHostController,
    isNewCard: Boolean,
    initialDate: Long? = null
) {
    val screenScope = rememberCoroutineScope()
    val cardId = cardViewModel.getCardId()
    val editorReady = remember(cardViewModel, initialDate, isNewCard) { mutableStateOf(false) }

    DisposableEffect(cardViewModel) {
        onDispose { cardViewModel.flushPendingCardEdits() }
    }

    LaunchedEffect(cardId, initialDate, isNewCard) {
        if (cardId == null) return@LaunchedEffect

        // A date-created card receives its schedule before the form becomes editable.
        if (isNewCard && initialDate != null) {
            cardViewModel.setScheduleAndAwait(
                Schedule(
                    pattern = Schedule.Pattern(
                        days = LinkedHashMap(),
                        type = FilterType.DATE
                    ),
                    dates = Schedule.Dates(addedDates = arrayOf(initialDate))
                )
            )
        }

        val lists = snapshotFlow { listsViewModel.nestedListState.value }
            .first { it.isNotEmpty() }
        val noteList = lists.filterIsInstance<NList>()
            .firstOrNull { it.sublistChain == cardViewModel.getSublistChane() }
        val currentSelection = listsViewModel.selectedListState.value as? NList
        val initialSelection = if (isNewCard) currentSelection ?: noteList else noteList
        if (initialSelection != null) {
            listsViewModel.selectedListState.value = initialSelection
        }

        var persistedSublistChain = cardViewModel.getSublistChane()
        if (isNewCard && initialSelection != null
            && initialSelection.sublistChain != persistedSublistChain
        ) {
            listsViewModel.bindListToEntity(initialSelection.sublistChain.toString(), cardId)
            cardViewModel.updateList(initialSelection)
            persistedSublistChain = initialSelection.sublistChain
        }

        editorReady.value = true

        snapshotFlow { listsViewModel.selectedListState.value as? NList }
            .filterNotNull()
            .collect { selectedList ->
                val selectedChain = selectedList.sublistChain
                if (selectedChain != persistedSublistChain) {
                    listsViewModel.bindListToEntity(selectedChain.toString(), cardId)
                    cardViewModel.updateList(selectedList)
                    persistedSublistChain = selectedChain
                }
            }
    }

    val cardAction = cardViewModel.cardActionClicked.collectAsState(initial = null)
    cardAction.value?.let { action ->
        cardViewModel.onCardActionClickFired()
        when (action) {
            CardAction.EDIT_SCHEDULE -> screenScope.launch {
                cardViewModel.flushAndAwaitCardEdits()
                navController.navigate("taskSchedule/${cardId}")
            }
            CardAction.SELECT_LIST -> listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
            CardAction.SELECT_TAGS -> tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
        }
    }

    if (cardId == null || !editorReady.value) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        AddTagsScreen(tagsViewModel, cardViewModel) {
            Scaffold(
                topBar = {
                    Box(modifier = Modifier.statusBarsPadding()) {
                        NoteBar(navController, cardViewModel, isNewCard = isNewCard)
                    }
                },
                content = { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
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
}
