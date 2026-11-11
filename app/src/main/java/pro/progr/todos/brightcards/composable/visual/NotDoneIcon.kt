package pro.progr.todos.brightcards.composable.visual

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

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
import pro.progr.todos.CardRow

@Composable
fun NotDoneIcon(row: CardRow) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = stringResource(R.string.not_done),
        tint = if (row.content.value.fillTitleBackground) Color(row.content.value.style.textColor())
                    else MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(5.dp).size(16.dp)

    )
}