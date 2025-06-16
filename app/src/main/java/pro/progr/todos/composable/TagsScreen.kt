package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.TagsViewModel

//TagsModalDrawer

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TagsScreen(viewModel: TagsViewModel,
               cardsListViewModel: CardsListViewModel,
               mainContent : @Composable () -> Unit) {
    val tagsFlow = viewModel.getAllTags().collectAsState(initial = null)

    // Используйте состояние для ModalDrawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Отслеживание состояния закрытия дровера
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }.collect { isClosed ->
            if (isClosed) {
                viewModel.showTagsSheet.value = false // Вызываем, когда дровер закрыт
            } else {
                viewModel.showTagsSheet.value = true
            }
        }
    }

    // Обновляем состояние шторки в зависимости от переменной showTagsSheet
    LaunchedEffect( viewModel.showTagsSheet.value) {
        if (viewModel.showTagsSheet.value) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    TextButton(onClick = {
                        cardsListViewModel.clearTags()
                        viewModel.showTagsSheet.value = false
                    },
                        modifier = Modifier
                            .padding(top = 46.dp)) {
                        Text(text = "Не выбирать тег",
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colors.onSurface)
                    }

                    LazyColumn {
                        tagsFlow.value?.let { tags ->
                            itemsIndexed(tags) { _, tag ->
                                TextButton(onClick = {
                                    cardsListViewModel.updateTag(tag)
                                    viewModel.showTagsSheet.value = false
                                }) {
                                    Text(text = "#${tag.title}",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colors.onSurface)
                                }
                            }
                        }
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    mainContent()
                }
            }
        )
    }
}