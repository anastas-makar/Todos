package pro.progr.todos.brightcards.composable.visual

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.vm.ListedCardViewModel

@Composable
fun NotDoneIcon(viewModel: ListedCardViewModel) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "не сделано",
        tint = if (viewModel.card.value.fillTitleBackground) Color(viewModel.card.value.style.textColor())
                    else MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(5.dp).size(16.dp)

    )
}