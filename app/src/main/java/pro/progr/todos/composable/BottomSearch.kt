package pro.progr.todos.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.progr.todos.R

@Composable
fun BottomSearch(
    searchActive: Boolean,
    showAllTemplates: Boolean,
    onSearchToggle: () -> Unit,
    onModeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconToggleButton(
                checked = searchActive,
                onCheckedChange = { onSearchToggle() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (searchActive) MaterialTheme.colors.primary.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_icon),
                    tint = if (searchActive) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                )
            }

            TextButton(onClick = onModeToggle, modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(if (showAllTemplates) R.string.all_templates else R.string.not_in_calendar),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
