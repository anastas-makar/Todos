package pro.progr.todos

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pro.progr.diamondapi.GetDiamondsCountInterface
import pro.progr.todos.composable.pages.EditCardScreen
import pro.progr.lists.ListsViewModel
import pro.progr.todos.brightcards.colors.ColorsProvider
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.composable.pages.CalendarScreen
import pro.progr.todos.composable.pages.CreateCardForDateScreen
import pro.progr.todos.composable.pages.CreateCardScreen
import pro.progr.todos.composable.pages.EditNoteInHistoryScreen
import pro.progr.todos.composable.pages.ListScreen
import pro.progr.todos.composable.pages.SchedulePage
import pro.progr.todos.dagger2.CardViewModelFactory
import pro.progr.todos.dagger2.ListsViewModelFactory
import pro.progr.todos.dagger2.NoteCalendarViewModelFactory
import pro.progr.todos.dagger2.TodosComponent

@kotlinx.coroutines.ExperimentalCoroutinesApi
@Composable
fun TodosNavigation(
    appDrawer : @Composable (drawerState: DrawerState,
                             diamondViewModel: GetDiamondsCountInterface,
                             externalNavController: NavHostController,
                             content : @Composable () -> Unit) -> Unit,
    externalNavController: NavHostController,
    diamondViewModel : DiamondViewModel,
    component: TodosComponent
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val daggerViewModelFactory = component.daggerViewModelFactory()
    val listedCardViewModelFactory = component.listedCardViewModelFactory()
    val cardViewModelFactory: CardViewModelFactory = component.cardViewModelFactory()
    val noteCalendarViewModelFactory: NoteCalendarViewModelFactory = component.noteCalendarViewModelFactory()
    val listsViewModelFactory: ListsViewModelFactory = component.listsViewModelFactory()

    val commonListsViewModel: ListsViewModel = viewModel(factory = listsViewModelFactory)
    val cardsListViewModel : CardsListViewModel = viewModel(factory = daggerViewModelFactory)
    val paletteViewModel : PaletteViewModel = viewModel(factory = component.paletteViewModelFactory())

    val paletteViewModelState = remember { mutableStateOf(paletteViewModel) }
    val historyState = cardsListViewModel.getHistoryFlow().collectAsState(null)

    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!

    cardsListViewModel.setViewModelCreator { note, cardContent, date ->
        listedCardViewModelFactory
            .createWithCardContent(cardContent)
            .createWithNote(note)

        ViewModelProvider(viewModelStoreOwner, listedCardViewModelFactory).get(note.toString() + date?.toString(), ListedCardViewModel::class.java)
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {

            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            CalendarScreen(
                appDrawer = appDrawer,
                diamondViewModel = diamondViewModel,
                externalNavController = externalNavController,
                navController = navController,
                drawerState = drawerState,
                cardsListViewModel = cardsListViewModel,
                commonListsViewModel = commonListsViewModel,
                tagsViewModel = tagsViewModel,
                historyState = historyState
            )

        }
        composable("list") {
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            ListScreen(
                appDrawer = appDrawer,
                diamondViewModel = diamondViewModel,
                navController = navController,
                externalNavController = externalNavController,
                drawerState = drawerState,
                cardsListViewModel = cardsListViewModel,
                commonListsViewModel = commonListsViewModel,
                tagsViewModel = tagsViewModel
            )
        }

        composable("editCard/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            cardViewModelFactory.cardId = cardId
            val cardViewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)
            cardViewModel.getCard(cardId!!)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "editCard${cardId}")

            if (cardViewModel.cardContent.value.id != null) {
                EditCardScreen(
                    cardViewModel = cardViewModel,
                    tagsViewModel = tagsViewModel,
                    listsViewModel = listsViewModel,
                    cardId = cardId,
                    paletteViewModelState = paletteViewModelState,
                    navController = navController
                )

            }

        }

        composable("createCard") {
            cardViewModelFactory.cardId = null
            cardViewModelFactory.style =
                ColorsProvider.getPalette(paletteViewModel.getDefaultPalette().paletteType).random()
            val cardViewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "createCard")

            CreateCardScreen(
                cardViewModel = cardViewModel,
                tagsViewModel = tagsViewModel,
                listsViewModel = listsViewModel,
                paletteViewModelState = paletteViewModelState,
                navController = navController
            )

        }
        composable("createCardForDate/{epochDay}") { backStackEntry ->
            val epochDay = backStackEntry.arguments?.getString("epochDay")

            cardViewModelFactory.cardId = null
            cardViewModelFactory.style =
                ColorsProvider.getPalette(paletteViewModel.getDefaultPalette().paletteType).random()
            val viewModel: CardViewModel = viewModel(factory = cardViewModelFactory)
            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            val listsViewModel: ListsViewModel =
                viewModel(factory = listsViewModelFactory, key = "createCard${epochDay}")

            CreateCardForDateScreen(
                cardViewModel = viewModel,
                tagsViewModel = tagsViewModel,
                listsViewModel = listsViewModel,
                paletteViewModelState = paletteViewModelState,
                navController = navController,
                noteCalendarViewModelFactory = noteCalendarViewModelFactory,
                epochDay = epochDay!!.toLong()
            )
        }

        composable("taskSchedule/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            noteCalendarViewModelFactory.noteId = cardId!!
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

                EditNoteInHistoryScreen(
                    cardViewModel = cardViewModel,
                    listsViewModel = listsViewModel,
                    epochDay = epochDay.toLong(),
                    paletteViewModelState = paletteViewModelState,
                    navController = navController
                )

            }


        }
    }
}