package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import pro.progr.todos.CardViewModel
import pro.progr.todos.TagsViewModel

//TagsModalDrawer

@Composable
fun AddTagsScreen(tagsViewModel: TagsViewModel,
                  cardViewModel: CardViewModel,
                  mainContent : @Composable () -> Unit) {
    val tagsFlow = tagsViewModel.getAllTags().collectAsState(initial = null)
    val text = remember { mutableStateOf("") }

    // Используйте состояние для ModalDrawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Отслеживание состояния закрытия дровера
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isClosed }.collect { isClosed ->
            if (isClosed) {
                tagsViewModel.showTagsSheet.value = false
            } else {
                tagsViewModel.showTagsSheet.value = true
            }
        }
    }

    // Обновляем состояние шторки в зависимости от переменной showTagsSheet
    LaunchedEffect(tagsViewModel.showTagsSheet.value) {
        if (tagsViewModel.showTagsSheet.value) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {

                    // Текстовое поле и кнопка
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 30.dp)) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            TextField(
                                value = text.value,
                                onValueChange = { v:String -> text.value = v },
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.Transparent),
                                placeholder = { Text(text = "тег") }
                            )
                        }
                        IconButton(onClick = {
                            cardViewModel.inserAndAddTag(text.value)
                            text.value = ""
                            tagsViewModel.showTagsSheet.value = false
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Tag")
                        }
                    }

                    // Список тегов
                    LazyColumn(Modifier.padding(top = 8.dp)) {
                        tagsFlow.value?.let { tags ->
                            itemsIndexed(tags) { _, tag ->

                                if (cardViewModel
                                        .cardContent.value.tags.any { noteTag -> noteTag.id == tag.id }
                                ) {
                                    Row (verticalAlignment = Alignment.CenterVertically) {

                                        Icon(
                                            imageVector = Icons.Outlined.Done,
                                            contentDescription = "Done",
                                            tint = MaterialTheme.colors.onSurface,
                                            modifier = Modifier.height(12.dp)
                                        )

                                        TextButton(onClick = {
                                            cardViewModel.removeNoteTag(tag)
                                            tagsViewModel.showTagsSheet.value = false
                                        }) {
                                            Text(text = "${tag.title}#",
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                color = MaterialTheme.colors.onSurface)
                                        }
                                    }

                                } else {
                                    TextButton(onClick = {
                                        cardViewModel.addTag(tag)
                                        tagsViewModel.showTagsSheet.value = false
                                    }) {
                                        Text(text = "${tag.title}#",
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = MaterialTheme.colors.onSurface)
                                    }
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
