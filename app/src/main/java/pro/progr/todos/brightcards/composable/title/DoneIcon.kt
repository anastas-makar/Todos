package pro.progr.todos.brightcards.composable.title

import androidx.compose.ui.res.stringResource
import pro.progr.todos.R

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
import pro.progr.todos.CardRow

@Composable
fun DoneIcon(row: CardRow) {
    Icon(
        imageVector = Icons.Outlined.Done,
        contentDescription = stringResource(R.string.done),
        tint = if (row.content.value.fillTitleBackground)
                    Color(row.content.value.style.textColor())
                else
                    MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(5.dp).size(16.dp)
    )
}