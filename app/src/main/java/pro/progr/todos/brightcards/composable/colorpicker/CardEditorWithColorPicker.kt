package pro.progr.brightcards.composable.colorpicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pro.progr.brightcards.composable.EditCard
import pro.progr.brightcards.composable.visual.ColorPickerButton
import pro.progr.brightcards.vm.CardViewModel
import pro.progr.brightcards.vm.PaletteViewModel

@ExperimentalMaterialApi
@Composable
fun CardEditorWithColorPicker(cardViewModel: CardViewModel, paletteViewModel: PaletteViewModel) {

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
        scrimColor = Color.Black.copy(alpha = 0.32f),  // Установка scrim цвета
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = if (sheetState.isVisible) 5.dp else 0.dp,
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
        Box(modifier = Modifier.fillMaxSize()) {
            EditCard(
                cardViewModel = cardViewModel
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, bottom = 16.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ColorPickerButton(onClick = {
                    coroutineScope.launch {
                        if (sheetState.isVisible) {
                            sheetState.hide()
                        } else {
                            sheetState.show()
                        }
                    }
                }, cardViewModel = cardViewModel)
            }
        }
    }
}
