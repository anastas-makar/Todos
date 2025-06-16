package pro.progr.doflow.composable

import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import pro.progr.doflow.CardsListViewModel
import pro.progr.doflow.TagsViewModel
import pro.progr.lists.ListSelector
import pro.progr.lists.ListsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ListModeBar(navController : NavHostController,
                viewModel: ListsViewModel,
                tagsViewModel: TagsViewModel,
                cardsListViewModel: CardsListViewModel,
                drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = { ListSelector(viewModel = viewModel) },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    painter = painterResource(id = pro.progr.doflow.R.drawable.ic_sunduk_icon),
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

            IconButton(onClick = { navController.navigate("calendar") }) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}