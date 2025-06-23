package pro.progr.todos

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pro.progr.lists.ListsViewModel
import pro.progr.todos.composable.pages.CalendarScreen

@OptIn(ExperimentalMaterialApi::class)
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Composable
fun AppNavigation(
    appDrawer : @Composable (drawerState: DrawerState,
                             viewModel: DiamondViewModel,
                             navController: NavHostController,
                             content : @Composable () -> Unit) -> Unit,
    diamondViewModel: DiamondViewModel,
    navController: NavHostController,
    drawerState: DrawerState
) {

    val paletteViewModelState = remember { mutableStateOf(paletteViewModel) }

    val commonListsViewModel: ListsViewModel = viewModel(factory = listsViewModelFactory)

    NavHost(navController = navController, startDestination = "todos") {
        composable("todos") {

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