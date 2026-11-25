package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Done
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardViewModel
import pro.progr.todos.R
import pro.progr.todos.TagsViewModel

@Composable
fun AddTagsScreen(
    tagsViewModel: TagsViewModel,
    cardViewModel: CardViewModel,
    mainContent: @Composable () -> Unit
) {
    val tags = tagsViewModel.getAllTags().collectAsState(initial = null).value.orEmpty()
    var text by remember { mutableStateOf("") }
    var managingTags by remember { mutableStateOf(false) }
    val selectedIds = cardViewModel.cardContent.value.tags.map { it.id }.toSet()
    val drawerState = androidx.compose.material.rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }.collect { isClosed ->
            tagsViewModel.showTagsSheet.value = !isClosed
            if (isClosed) managingTags = false
        }
    }
    LaunchedEffect(tagsViewModel.showTagsSheet.value) {
        if (tagsViewModel.showTagsSheet.value) drawerState.open() else drawerState.close()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    if (managingTags) {
                        TextButton(
                            onClick = { managingTags = false },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(stringResource(R.string.back), color = MaterialTheme.colors.onSurface)
                        }
                        LazyColumn(Modifier.padding(top = 8.dp)) {
                            items(tags.sortedBy { it.title }, key = { it.id }) { tag ->
                                val cannotHide = !tag.hidden && tag.id in selectedIds
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${tag.title}#",
                                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                        color = MaterialTheme.colors.onSurface
                                    )
                                    TextButton(
                                        enabled = !cannotHide,
                                        onClick = {
                                            tagsViewModel.setHidden(
                                                tagId = tag.id,
                                                hidden = !tag.hidden,
                                                protectedTagIds = selectedIds
                                            )
                                        }
                                    ) {
                                        Text(
                                            stringResource(
                                                when {
                                                    tag.hidden -> R.string.show_tag
                                                    cannotHide -> R.string.tag_selected_on_card
                                                    else -> R.string.hide_tag
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                TextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    modifier = Modifier.weight(1f).wrapContentHeight(),
                                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                                    placeholder = { Text(text = stringResource(R.string.tag_hint)) }
                                )
                            }
                            IconButton(
                                enabled = text.trim().isNotEmpty(),
                                onClick = {
                                    cardViewModel.inserAndAddTag(text)
                                    text = ""
                                    tagsViewModel.showTagsSheet.value = false
                                }
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_tag))
                            }
                        }

                        LazyColumn(Modifier.padding(top = 8.dp)) {
                            items(
                                tags.filter { !it.hidden || it.id in selectedIds }.sortedBy { it.title },
                                key = { it.id }
                            ) { tag ->
                                val selected = tag.id in selectedIds
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Outlined.Done,
                                            contentDescription = stringResource(R.string.done),
                                            tint = MaterialTheme.colors.onSurface,
                                            modifier = Modifier.height(12.dp)
                                        )
                                    }
                                    TextButton(onClick = {
                                        if (selected) cardViewModel.removeNoteTag(tag)
                                        else cardViewModel.addTag(tag)
                                        tagsViewModel.showTagsSheet.value = false
                                    }) {
                                        Text(
                                            text = "${tag.title}#",
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = MaterialTheme.colors.onSurface
                                        )
                                    }
                                }
                            }
                            item(key = "manage_tags_footer") {
                                TextButton(
                                    onClick = { managingTags = true },
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.manage_tags),
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
