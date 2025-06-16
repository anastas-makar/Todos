package pro.progr.todos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pro.progr.brightcards.colors.ColorsProvider
import pro.progr.brightcards.composable.colorpicker.CardEditorWithColorPicker
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.vm.PaletteViewModel
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.flow.composable.CalendarWithFloatingElement
import pro.progr.flow.vm.FloatingElementViewModel
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import pro.progr.todos.composable.AddTagsScreen
import pro.progr.todos.composable.BottomSearch
import pro.progr.todos.composable.CalendarModeBar
import pro.progr.todos.composable.CardsList
import pro.progr.todos.composable.EditNoteBar
import pro.progr.todos.composable.EditNoteInHistoryBar
import pro.progr.todos.composable.ListModeBar
import pro.progr.todos.composable.MyFloatingActionButton
import pro.progr.todos.composable.NoteBar
import pro.progr.todos.composable.SmallActionsViewModel
import pro.progr.todos.composable.TagsScreen
import pro.progr.todos.composable.calendar.DayInHistory
import pro.progr.todos.composable.calendar.Future
import pro.progr.todos.composable.calendar.Today
import pro.progr.todos.composable.pages.SchedulePage
import pro.progr.todos.dagger2.CardViewModelFactory
import pro.progr.todos.dagger2.DaggerViewModelFactory
import pro.progr.todos.dagger2.ListsViewModelFactory
import pro.progr.todos.dagger2.NoteCalendarViewModelFactory
import pro.progr.todos.datefilters.FilterType
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Composable
fun AppNavigation(
    daggerViewModelFactory: DaggerViewModelFactory,
    cardViewModelFactory: CardViewModelFactory,
    noteCalendarViewModelFactory: NoteCalendarViewModelFactory,
    listsViewModelFactory: ListsViewModelFactory,
    paletteViewModel: PaletteViewModel,
    cardsListViewModel: CardsListViewModel,
    historyState: State<Map<Long, List<CardContent>>?>,
    diamondsCountRepository: DiamondsCountRepository,
    startDestination: String = "calendar"
) {

    val paletteViewModelState = remember { mutableStateOf(paletteViewModel) }

    val navController = rememberNavController()

    val commonListsViewModel: ListsViewModel = viewModel(factory = listsViewModelFactory)

    NavHost(navController = navController, startDestination = startDestination) {
        composable("calendar") {

            LaunchedEffect(Unit) {
                cardsListViewModel.flushNotesSearch()
                cardsListViewModel.deleteEmptyNotes()
            }

            if (commonListsViewModel.selectedListState.value != null) {
                cardsListViewModel.useList((commonListsViewModel.selectedListState.value as NList).sublistChain)
            }


            // Состояние для Drawer
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val diamondViewModel: DiamondViewModel = viewModel(factory = daggerViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            //AppDrawer(drawerState, diamondViewModel, navController) {
                LaunchedEffect(Unit) {
                    drawerState.close()
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
            //}
        }

        composable("editCard/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            cardViewModelFactory.cardId = cardId
            val cardViewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "editCard${cardId}")

            LaunchedEffect(Unit) {
                cardViewModel.getCard(cardId!!)

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

        composable("createCard") {
            cardViewModelFactory.cardId = null
            cardViewModelFactory.style =
                ColorsProvider.getPalette(paletteViewModel.getDefaultPalette().paletteType).random()
            val cardViewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            val annotation = cardViewModel.annotationClicked.collectAsState(initial = null)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "createCard")

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

            annotation.value?.let {
                cardViewModel.onAnnotationClickFired()
                if (it.tag == "schedule") {
                    navController.navigate("taskSchedule/${cardViewModel.getCardId()}")
                } else if (it.tag == "list") {
                    listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
                }  else if (it.tag == "tags") {
                    tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
                }
            }
        }
        composable("createCardForDate/{epochDay}") { backStackEntry ->
            val epochDay = backStackEntry.arguments?.getString("epochDay")

            cardViewModelFactory.cardId = null
            cardViewModelFactory.style =
                ColorsProvider.getPalette(paletteViewModel.getDefaultPalette().paletteType).random()
            val viewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            val annotation = viewModel.annotationClicked.collectAsState(initial = null)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "createCard${epochDay}")

            if (listsViewModel.selectedListState.value != null) {

                viewModel.getCardId()?.also { gotCardId ->
                    listsViewModel.bindListToEntity(
                        (listsViewModel.selectedListState.value as NList).sublistChain.toString(),
                        gotCardId
                    )

                    if (noteCalendarViewModelFactory.noteId != gotCardId.toLong()) {
                        noteCalendarViewModelFactory.noteId = gotCardId.toLong()
                        viewModel.setSchedule(Schedule(Schedule.Pattern(
                            days = LinkedHashMap(),
                            type = FilterType.DATE
                        ),
                            dates = Schedule.Dates(addedDates = arrayOf(epochDay!!.toLong()))))
                    }
                    viewModel.updateList(listsViewModel.selectedListState.value as NList)
                }

            }

            annotation.value?.let {
                viewModel.onAnnotationClickFired()
                if (it.tag == "schedule") {
                    navController.navigate("taskSchedule/${viewModel.getCardId()}")
                } else if (it.tag == "list") {
                    listsViewModel.sheetMode.value = ListsViewModel.SheetMode.SHOW
                }  else if (it.tag == "tags") {
                    tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value
                }
            }

            AddTagsScreen(tagsViewModel, viewModel) {
                Scaffold(
                    topBar = {
                        Box(modifier = Modifier.statusBarsPadding()) {
                            NoteBar(navController, viewModel)
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
                                CardEditorWithColorPicker(viewModel, paletteViewModelState.value)
                            }
                        }
                    }
                )
            }
        }
        composable("list") {

            LaunchedEffect(Unit) {
                cardsListViewModel.flushNotesList()
                cardsListViewModel.deleteEmptyNotes()
            }
            if (commonListsViewModel.selectedListState.value != null) {
                cardsListViewModel.useList((commonListsViewModel.selectedListState.value as NList).sublistChain)
            }
            // Состояние для Drawer
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            val diamondViewModel: DiamondViewModel = viewModel(factory = daggerViewModelFactory)

            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            //AppDrawer(drawerState, diamondViewModel, navController) {
                LaunchedEffect(Unit) {
                    drawerState.close()
                }

                TagsScreen(tagsViewModel, cardsListViewModel) {
                    Scaffold(
                        topBar = {
                            Box(modifier = Modifier.statusBarsPadding()) {
                                ListModeBar(navController,
                                    commonListsViewModel,
                                    tagsViewModel,
                                    cardsListViewModel,
                                    drawerState)
                            }
                        },
                        content = { innerPadding ->
                            val floatingElementViewModel = remember { FloatingElementViewModel() }

                            ListedContentFrame(listsViewModel = commonListsViewModel) {
                                Box(
                                    modifier = Modifier
                                        .padding(innerPadding)
                                        .fillMaxHeight()
                                        .navigationBarsPadding()
                                ) {
                                    CardsList(cardsListViewModel, navController)

                                    if (floatingElementViewModel.showScrim.value) {
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .background(color = Color(0xEEFFFFFF))
                                            .zIndex(1f)
                                            .clickable {
                                                floatingElementViewModel.showScrim.value = false
                                            }
                                        )
                                    }

                                    Column(modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .zIndex(2f)) {
                                        MyFloatingActionButton(
                                            floatingElementViewModel = floatingElementViewModel,
                                            SmallActionsViewModel(),
                                            navController
                                        )
                                        BottomSearch(cardsListViewModel)
                                    }
                                }
                            }
                        }
                    )

                }
                    //}
        }

        composable("taskSchedule/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            noteCalendarViewModelFactory.noteId = cardId!!.toLong()
            val viewModel: NoteCalendarViewModel = viewModel(factory = noteCalendarViewModelFactory)

            SchedulePage(viewModel, navController)
        }

        composable("editNoteInHistory/{cardId}/date/{date}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            val epochDay = backStackEntry.arguments?.getString("date")

            epochDay?.let {

                cardViewModelFactory.epochDay = epochDay.toLong()
                cardViewModelFactory.cardId = cardId

                val cardViewModel: EditCardInHistoryViewModel =
                    viewModel(factory = cardViewModelFactory)
                val listsViewModel: ListsViewModel = viewModel(factory = listsViewModelFactory)

                Scaffold(
                    modifier = Modifier
                        .navigationBarsPadding(),
                    topBar = {
                        Box(modifier = Modifier.statusBarsPadding()) {
                            EditNoteInHistoryBar(
                                navController = navController,
                                viewModel = cardViewModel,
                                date = LocalDate.ofEpochDay(epochDay.toLong())
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


        }

    }
}

