package pro.progr.brightcards.composable.visual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun NotActiveTodoSquare(viewModel: ListedCardViewModel) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(5.dp)
            .border(
                1.dp,
                if (viewModel.card.value.fillTitleBackground) Color(viewModel.card.value.style.secondaryElementsColor())
                else MaterialTheme.colors.onSurface, shape = RoundedCornerShape(4.dp)
            )
            .background(Color(0x44ffffff), shape = RoundedCornerShape(4.dp))
    )
}