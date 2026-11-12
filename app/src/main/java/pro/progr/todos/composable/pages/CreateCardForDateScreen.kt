package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pro.progr.todos.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.CardViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.composable.AddTagsScreen
import pro.progr.todos.composable.NoteBar
import pro.progr.todos.dagger2.NoteCalendarViewModelFactory
import pro.progr.todos.datefilters.FilterType
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateCardForDateScreen(cardViewModel: CardViewModel,
                            tagsViewModel: TagsViewModel,
                            listsViewModel: ListsViewModel,
                            paletteViewModelState: MutableState<PaletteViewModel>,
                            navController: NavHostController,
                            noteCalendarViewModelFactory: NoteCalendarViewModelFactory,
                            epochDay: Long
) {

    val cardAction = cardViewModel.cardActionClicked.collectAsState(initial = null)
    val cardId = cardViewModel.getCardId()
    val selectedList = listsViewModel.selectedListState.value as? NList

    LaunchedEffect(cardId, epochDay) {
        if (cardId != null && noteCalendarViewModelFactory.noteId != cardId) {
            noteCalendarViewModelFactory.noteId = cardId
            cardViewModel.setSchedule(
                Schedule(
                    Schedule.Pattern(
                        days = LinkedHashMap(),
                        type = FilterType.DATE
                    ),
                    dates = Schedule.Dates(addedDates = arrayOf(epochDay))
                )
            )
        }
    }

    LaunchedEffect(cardId, selectedList?.sublistChain?.toString()) {
        if (cardId != null && selectedList != null
            && selectedList.sublistChain != cardViewModel.getSublistChane()
        ) {
            listsViewModel.bindListToEntity(selectedList.sublistChain.toString(), cardId)
            cardViewModel.updateList(selectedList)
        }
    }

    cardAction.value?.let { action ->
        cardViewModel.onCardActionClickFired()
        when (action) {
            pro.progr.todos.brightcards.model.CardAction.EDIT_SCHEDULE -> navController.navigate("taskSchedule/${cardViewModel.getCardId()}")
            pro.progr.todos.brightcards.model.CardAction.SELECT_LIST -> listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
            pro.progr.todos.brightcards.model.CardAction.SELECT_TAGS -> tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
        }
    }

    if (cardId == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
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
    }
}
