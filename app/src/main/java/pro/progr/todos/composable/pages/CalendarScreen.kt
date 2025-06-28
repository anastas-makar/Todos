package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.composable.CalendarModeBar
import pro.progr.todos.composable.MyFloatingActionButton
import pro.progr.todos.composable.SmallActionsViewModel
import pro.progr.todos.composable.TagsScreen
import pro.progr.todos.composable.calendar.DayInHistory
import pro.progr.todos.composable.calendar.Future
import pro.progr.todos.composable.calendar.Today
import pro.progr.flow.composable.CalendarWithFloatingElement
import pro.progr.flow.vm.FloatingElementViewModel
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun CalendarScreen(
    appDrawer : @Composable (drawerState: DrawerState,
        viewModel: DiamondViewModel,
        externalNavController: NavHostController,
        content : @Composable () -> Unit) -> Unit,
    diamondViewModel: DiamondViewModel,
    externalNavController: NavHostController,
    navController: NavHostController,
    drawerState: DrawerState,
    cardsListViewModel: CardsListViewModel,
    commonListsViewModel: ListsViewModel,
    tagsViewModel: TagsViewModel,
    historyState: State<Map<Long, List<CardContent>>?>) {
    LaunchedEffect(Unit) {
        cardsListViewModel.flushNotesSearch()
        cardsListViewModel.deleteEmptyNotes()
        drawerState.close()
    }

    appDrawer(drawerState, diamondViewModel, externalNavController) {

        if (commonListsViewModel.selectedListState.value != null) {
            cardsListViewModel.useList((commonListsViewModel.selectedListState.value as NList).sublistChain)
        }

        TagsScreen(viewModel = tagsViewModel, cardsListViewModel = cardsListViewModel) {
            Scaffold(
                topBar = {
                    Box(modifier = Modifier.statusBarsPadding()) {
                        CalendarModeBar(navController,
                            commonListsViewModel,
                            tagsViewModel,
                            cardsListViewModel,
                            drawerState)
                    }
                },
                content = { innerPadding ->

                    val floatingElementViewModel = remember { FloatingElementViewModel() }
                    val smallActionsViewModel = SmallActionsViewModel()

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .navigationBarsPadding()
                    ) {
                        ListedContentFrame(listsViewModel = commonListsViewModel) {
                            CalendarWithFloatingElement(
                                content = { date: LocalDate ->
                                    val today = LocalDate.now()
                                    if (date.isEqual(today)) {
                                        Today(
                                            cardsListViewModel = cardsListViewModel,
                                            diamondViewModel = diamondViewModel,
                                            navController = navController,
                                            date = date,
                                            cardsForDate = cardsListViewModel.getHistoryViewModels(
                                                historyState.value?.get(date.toEpochDay())
                                                    ?: emptyList(), date)
                                        ) {
                                            smallActionsViewModel.selectedDate = today
                                            floatingElementViewModel.showScrim.value = true
                                        }
                                    } else if (date.isAfter(today)) {
                                        Future(
                                            cardsListViewModel = cardsListViewModel,
                                            navController = navController,
                                            date = date
                                        ) {
                                            smallActionsViewModel.selectedDate = date
                                            floatingElementViewModel.showScrim.value = true
                                        }
                                    } else {
                                        DayInHistory(
                                            cardsListViewModel = cardsListViewModel,
                                            diamondViewModel = diamondViewModel,
                                            navController = navController,
                                            date = date,
                                            cardsForDate = cardsListViewModel.getHistoryViewModels(
                                                historyState.value?.get(date.toEpochDay())
                                                    ?: emptyList(), date)
                                        )
                                    }
                                },
                                floatingElement = {
                                    MyFloatingActionButton(floatingElementViewModel = floatingElementViewModel,
                                        smallActionsViewModel,
                                        navController) },
                                floatingElementViewModel = floatingElementViewModel
                            )

                        }
                    }
                }
            )

        }

    }
}