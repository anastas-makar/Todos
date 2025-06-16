package pro.progr.brightcards.composable.title

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.brightcards.vm.ListedCardViewModel

@Composable
fun DoneIcon(viewModel: ListedCardViewModel) {
    Icon(
        imageVector = Icons.Outlined.Done,
        contentDescription = "Done",
        tint = if (viewModel.card.value.fillTitleBackground)
                    Color(viewModel.card.value.style.textColor())
                else
                    MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(5.dp).size(16.dp)
    )
}