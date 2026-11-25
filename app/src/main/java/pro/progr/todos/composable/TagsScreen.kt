package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.R
import pro.progr.todos.TagsViewModel

@Composable
fun TagsScreen(
    viewModel: TagsViewModel,
    cardsListViewModel: CardsListViewModel,
    mainContent: @Composable () -> Unit
) {
    val tags = viewModel.getAllTags().collectAsState(initial = null).value.orEmpty()
    val selectedTagId = cardsListViewModel.selectedTag.value?.id
    var managingTags by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }.collect { isClosed ->
            viewModel.showTagsSheet.value = !isClosed
            if (isClosed) managingTags = false
        }
    }
    LaunchedEffect(viewModel.showTagsSheet.value) {
        if (viewModel.showTagsSheet.value) drawerState.open() else drawerState.close()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    TextButton(
                        onClick = { managingTags = !managingTags },
                        modifier = Modifier.padding(top = 46.dp)
                    ) {
                        Text(
                            stringResource(if (managingTags) R.string.back else R.string.manage_tags),
                            color = MaterialTheme.colors.onSurface
                        )
                    }

                    if (managingTags) {
                        LazyColumn {
                            items(tags.sortedBy { it.title }, key = { it.id }) { tag ->
                                val cannotHide = !tag.hidden && tag.id == selectedTagId
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${tag.title}",
                                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                        color = MaterialTheme.colors.onSurface
                                    )
                                    TextButton(
                                        enabled = !cannotHide,
                                        onClick = {
                                            viewModel.setHidden(
                                                tagId = tag.id,
                                                hidden = !tag.hidden,
                                                protectedTagIds = setOfNotNull(selectedTagId)
                                            )
                                        }
                                    ) {
                                        Text(
                                            stringResource(
                                                when {
                                                    tag.hidden -> R.string.show_tag
                                                    cannotHide -> R.string.tag_selected_as_filter
                                                    else -> R.string.hide_tag
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        TextButton(onClick = {
                            cardsListViewModel.clearTags()
                            viewModel.showTagsSheet.value = false
                        }) {
                            Text(
                                text = stringResource(R.string.select_no_tag),
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colors.onSurface
                            )
                        }

                        LazyColumn {
                            items(
                                tags.filter { !it.hidden || it.id == selectedTagId }.sortedBy { it.title },
                                key = { it.id }
                            ) { tag ->
                                TextButton(onClick = {
                                    cardsListViewModel.updateTag(tag)
                                    viewModel.showTagsSheet.value = false
                                }) {
                                    Text(
                                        text = "#${tag.title}",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colors.onSurface
                                    )
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
