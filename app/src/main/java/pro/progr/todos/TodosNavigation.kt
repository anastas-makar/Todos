package pro.progr.todos

import android.app.Application
import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pro.progr.lists.ListsViewModel
import pro.progr.todos.brightcards.vm.ListedCardViewModel
import pro.progr.todos.composable.pages.CalendarScreen
import pro.progr.todos.dagger2.AppModule
import pro.progr.todos.dagger2.DaggerTodosComponent

@kotlinx.coroutines.ExperimentalCoroutinesApi
@Composable
fun TodosNavigation(
    appDrawer : @Composable (drawerState: DrawerState,
                             diamondViewModel: DiamondViewModel,
                             navController: NavHostController,
                             content : @Composable () -> Unit) -> Unit,
    diamondViewModel: DiamondViewModel,
    navController: NavHostController,
    drawerState: DrawerState
) {

    val appContext = LocalContext.current.applicationContext

    val component = remember { DaggerTodosComponent.builder()
        .application(appContext as Application)
        .appModule(AppModule(appContext as Application))
        .build() }

    val daggerViewModelFactory = component.daggerViewModelFactory()
    val listedCardViewModelFactory = component.listedCardViewModelFactory()

    val commonListsViewModel: ListsViewModel = viewModel(factory = component.listsViewModelFactory())
    val cardsListViewModel : CardsListViewModel = viewModel(factory = daggerViewModelFactory)

    val historyState = cardsListViewModel.getHistoryFlow().collectAsState(null)

    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!

    cardsListViewModel.setViewModelCreator { note, cardContent, date ->
        listedCardViewModelFactory
            .createWithCardContent(cardContent)
            .createWithNote(note)

        ViewModelProvider(viewModelStoreOwner, listedCardViewModelFactory).get(note.toString() + date?.toString(), ListedCardViewModel::class.java)
    }

    NavHost(navController = navController, startDestination = "calendar") {
        composable("calendar") {

            val tagsViewModel: TagsViewModel = viewModel(factory = daggerViewModelFactory)

            CalendarScreen(
                appDrawer = appDrawer,
                diamondViewModel = diamondViewModel,
                navController = navController,
                drawerState = drawerState,
                cardsListViewModel = cardsListViewModel,
                commonListsViewModel = commonListsViewModel,
                tagsViewModel = tagsViewModel,
                historyState = historyState
            )

        }
    }
}