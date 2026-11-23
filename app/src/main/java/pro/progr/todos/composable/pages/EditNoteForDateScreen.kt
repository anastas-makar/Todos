package pro.progr.todos.composable.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pro.progr.todos.EditCardForDateViewModel
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.composable.colorpicker.ColorPicker
import pro.progr.todos.brightcards.composable.description.EditDescription
import pro.progr.todos.brightcards.composable.title.EditTitle
import pro.progr.todos.brightcards.composable.visual.ColorPickerButton
import pro.progr.todos.brightcards.vm.PaletteViewModel
import pro.progr.todos.composable.EditNoteDateEntryBar
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditNoteDateEntryScreen(
    cardViewModel: EditCardForDateViewModel,
    epochDay: Long,
    navController: NavHostController,
    paletteViewModel: PaletteViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    if (!sheetState.isVisible) {
        paletteViewModel.swipeToDefault()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.32f),
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = if (sheetState.isVisible) 5.dp else 0.dp,
        sheetGesturesEnabled = sheetState.isVisible,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(2.dp)
            ) {
                Divider(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(40.dp)
                        .padding(top = 2.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )
            }
            ColorPicker(cardViewModel, paletteViewModel)
        },
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    EditNoteDateEntryBar(
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
                            .imePadding()
                ) {
                    if (!cardViewModel.editorReady.value) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    color = style.titleBackgroundColor,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                ),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            ColorPickerButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (sheetState.isVisible) sheetState.hide() else sheetState.show()
                                    }
                                },
                                colorUpdater = cardViewModel
                            )
                        }
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
}
