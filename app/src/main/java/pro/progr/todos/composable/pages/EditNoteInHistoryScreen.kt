package pro.progr.todos.composable.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.composable.description.EditDescription
import pro.progr.todos.brightcards.composable.title.EditTitle
import pro.progr.todos.EditCardInHistoryViewModel
import pro.progr.todos.composable.EditNoteInHistoryBar
import pro.progr.lists.ListedContentFrame
import pro.progr.lists.ListsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditNoteInHistoryScreen(cardViewModel: EditCardInHistoryViewModel,
                            listsViewModel: ListsViewModel,
                            epochDay: Long,
                            navController: NavHostController
) {
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                EditNoteInHistoryBar(
                    navController = navController,
                    viewModel = cardViewModel,
                    date = LocalDate.ofEpochDay(epochDay)
                )
            }
        },
        content = { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .navigationBarsPadding()
            ) {
                ListedContentFrame(listsViewModel = listsViewModel) {
                    val contentState = cardViewModel.getCardContent()
                    val content = contentState.value
                    val style = remember(contentState) { CardStyle(contentState) }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 5.dp)
                            .fillMaxSize()
                            .background(Color.Transparent, RoundedCornerShape(5.dp))
                            .border(1.dp, Color(content.style.backgroundColor()), RoundedCornerShape(5.dp))
                    ) {
                        EditTitle(
                            cardId = content.id,
                            title = content.title,
                            style = style,
                            onTitleChange = cardViewModel::updateCardTitle
                        )
                        EditDescription(
                            cardId = content.id,
                            description = content.text,
                            style = style,
                            onDescriptionChange = cardViewModel::updateCardDescription
                        )
                    }
                }
            }
        }
    )
}
