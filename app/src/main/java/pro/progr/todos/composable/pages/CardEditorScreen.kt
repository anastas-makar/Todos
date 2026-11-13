package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
    val cardLoadState = cardViewModel.cardLoadState.collectAsState()
    val initialScheduleReady = remember(cardViewModel, initialDate, isNewCard) {
        mutableStateOf(!isNewCard || initialDate == null)
    }

    DisposableEffect(cardViewModel) {
        onDispose { cardViewModel.flushPendingCardEdits() }
    }

    LaunchedEffect(cardId, initialDate, isNewCard, cardLoadState.value) {
        if (cardId == null || cardLoadState.value != CardViewModel.CardLoadState.READY) {
            return@LaunchedEffect
        }

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
            initialScheduleReady.value = true
        }

        // List loading is independent of card loading: a slow or missing list emission
        // must not keep the editor behind its loading indicator. Keep this work in the
        // same coroutine so it cannot race schedule initialization above.
        val lists = snapshotFlow { listsViewModel.nestedListState.value }
            .first { it.isNotEmpty() }
        val noteList = lists.filterIsInstance<NList>()
            .firstOrNull { it.sublistChain == cardViewModel.getSublistChane() }
        val currentSelection = listsViewModel.selectedListState.value as? NList
        val initialSelection = if (isNewCard) currentSelection ?: noteList else noteList
        if (initialSelection == null) return@LaunchedEffect

        listsViewModel.selectedListState.value = initialSelection
        cardViewModel.updateCardLabels(initialSelection.getName())
        var persistedSublistChain = cardViewModel.getSublistChane()
        if (isNewCard && initialSelection.sublistChain != persistedSublistChain) {
            listsViewModel.bindListToEntity(initialSelection.sublistChain.toString(), cardId)
            cardViewModel.updateList(initialSelection)
            persistedSublistChain = initialSelection.sublistChain
        }

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

    if (cardLoadState.value == CardViewModel.CardLoadState.FAILED) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(pro.progr.todos.R.string.card_load_failed))
                Button(
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = { cardViewModel.retryCardLoad() }
                ) {
                    Text(stringResource(pro.progr.todos.R.string.retry))
                }
            }
        }
    } else if (cardLoadState.value != CardViewModel.CardLoadState.READY
        || cardId == null || !initialScheduleReady.value
    ) {
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
