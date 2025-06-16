package pro.progr.todos.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.R
import pro.progr.todos.TagsViewModel
import pro.progr.lists.ListSelector
import pro.progr.lists.ListsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CalendarModeBar(navController : NavHostController,
                    listsViewModel: ListsViewModel,
                    tagsViewModel: TagsViewModel,
                    cardsListViewModel: CardsListViewModel,
                    drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = { ListSelector(viewModel = listsViewModel) },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sunduk_icon),
                    tint = Color.Unspecified,
                    contentDescription = "DiamondPath"
                )

            }
        },
        actions = {

            TextButton(onClick = { tagsViewModel.showTagsSheet.value = !tagsViewModel.showTagsSheet.value }) {
                Text(text= if (cardsListViewModel.selectedTag.value == null) "#"
                                else "#${cardsListViewModel.selectedTag.value?.title}",
                    color = MaterialTheme.colors.onSurface)
            }

            IconButton(onClick = { navController.navigate("list") }) {
                Box {
                    // Основная иконка
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    // Иконка крестика поверх
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).padding(top=3.dp),
                        tint = Color(0xff880e4f)
                    )
                }
            }

        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}