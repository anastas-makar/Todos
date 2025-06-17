package pro.progr.todos.brightcards.composable.visual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pro.progr.todos.R
import pro.progr.todos.brightcards.vm.CardViewModel

@Composable
fun ColorPickerButton(onClick: () -> Unit, cardViewModel: CardViewModel) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .border(1.dp,
                if (cardViewModel.getCardContent().value.fillTextBackground) Color(cardViewModel.getCardContent().value.style.iconsColor())
                else MaterialTheme.colors.onSurface
                , shape = RoundedCornerShape(4.dp))
            .background(Color.Transparent)
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (cardViewModel.getCardContent().value.fillTextBackground) Color(cardViewModel.getCardContent().value.style.iconsColor())
                    else MaterialTheme.colors.onSurface
                    , shape = RoundedCornerShape(100))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_palette),
                contentDescription = "Todo",
                tint = if (cardViewModel.getCardContent().value.fillTextBackground) Color(cardViewModel.getCardContent().value.style.backgroundColor())
                        else MaterialTheme.colors.background
            )
        }
    }
}