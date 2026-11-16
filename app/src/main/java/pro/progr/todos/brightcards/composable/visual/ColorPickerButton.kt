package pro.progr.todos.brightcards.composable.visual

import androidx.compose.ui.res.stringResource

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
import pro.progr.todos.CardViewModel

@Composable
fun ColorPickerButton(onClick: () -> Unit, cardViewModel: CardViewModel) {
    val backgroundColor = Color(cardViewModel.getCardContent().value.style.separateIconBackgroundColor())
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.Transparent)
            .padding(4.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(100))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_palette),
            contentDescription = stringResource(R.string.todo),
            tint = Color(cardViewModel.getCardContent().value.style.separateIconColor())
        )
    }
}