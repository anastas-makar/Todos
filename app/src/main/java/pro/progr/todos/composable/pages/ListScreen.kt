package pro.progr.todos.composable.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.flow.vm.FloatingElementViewModel
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.NList
import pro.progr.todos.TagsViewModel
import pro.progr.todos.composable.BottomSearch
import pro.progr.todos.composable.CardsList
import pro.progr.todos.composable.ListModeBar
import pro.progr.todos.composable.MyFloatingActionButton
import pro.progr.todos.composable.SearchBar
import pro.progr.todos.composable.SmallActionsViewModel
import pro.progr.todos.composable.ScheduleReturnSnackbar
import pro.progr.todos.R
import kotlinx.coroutines.launch
import pro.progr.todos.composable.TagsScreen

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun ListScreen(
    appDrawer : @Composable (drawerState: DrawerState,
        viewModel: DiamondViewModel,
        navController: NavHostController,
        content : @Composable () -> Unit) -> Unit,
    diamondViewModel: DiamondViewModel,
    navController: NavHostController,
    externalNavController: NavHostController,
    drawerState: DrawerState,
    cardsListViewModel: CardsListViewModel,
    commonListsViewModel: ListsViewModel,
    tagsViewModel: TagsViewModel) {

    LaunchedEffect(Unit) {
        cardsListViewModel.deleteEmptyNotes()
        drawerState.close()
    }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showAllTemplates by rememberSaveable { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(currentRoute, searchActive, searchQuery, showAllTemplates) {
        if (currentRoute == "list") {
            cardsListViewModel.updateListQuery(
                showAllTemplates = showAllTemplates,
                searchString = if (searchActive) searchQuery else ""
            )
        }
    }

    LaunchedEffect(searchActive) {
        if (searchActive) {
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    if (commonListsViewModel.selectedListState.value != null) {
        cardsListViewModel.useList((commonListsViewModel.selectedListState.value as NList).sublistChain)
    }


    appDrawer(drawerState, diamondViewModel, externalNavController) {

        TagsScreen(tagsViewModel, cardsListViewModel) {
            Scaffold(
                scaffoldState = scaffoldState,
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
                    ScheduleReturnSnackbar("list", navController, scaffoldState)
                    val floatingElementViewModel = remember { FloatingElementViewModel() }

                    ListedContentFrame(listsViewModel = commonListsViewModel) {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxHeight()
                                .navigationBarsPadding()
                                .imePadding()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 56.dp)
                            ) {
                                if (searchActive) {
                                    Surface(
                                        color = MaterialTheme.colors.surface,
                                        elevation = 2.dp
                                    ) {
                                        SearchBar(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            modifier = Modifier
                                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                                .focusRequester(searchFocusRequester)
                                        )
                                    }
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    CardsList(
                                        cardsListViewModel = cardsListViewModel,
                                        diamondViewModel = diamondViewModel,
                                        navController = navController,
                                        bottomContentPadding = 0.dp,
                                        onDoneMarked = {
                                            scope.launch {
                                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.task_done_in_calendar_notice),
                                                    actionLabel = context.getString(R.string.open_calendar)
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    navController.navigate("calendar")
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            if (floatingElementViewModel.showScrim.value && !searchActive) {
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color(0xEEFFFFFF))
                                    .zIndex(1f)
                                    .clickable {
                                        floatingElementViewModel.showScrim.value = false
                                    }
                                )
                            }

                            if (!searchActive) {
                                MyFloatingActionButton(
                                    floatingElementViewModel = floatingElementViewModel,
                                    SmallActionsViewModel(),
                                    navController,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .zIndex(1f)
                                        .padding(bottom = 56.dp)
                                )
                            }

                            BottomSearch(
                                searchActive = searchActive,
                                showAllTemplates = showAllTemplates,
                                onSearchToggle = {
                                    searchActive = !searchActive
                                    if (searchActive) {
                                        floatingElementViewModel.showScrim.value = false
                                    } else {
                                        searchQuery = ""
                                    }
                                },
                                onModeToggle = { showAllTemplates = !showAllTemplates },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .zIndex(2f)
                            )
                        }
                    }
                }
            )

        }

    }
}
