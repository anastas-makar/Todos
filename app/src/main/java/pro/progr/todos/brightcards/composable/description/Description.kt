package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import pro.progr.todos.brightcards.CardStyle
import pro.progr.todos.brightcards.model.CardTag
import pro.progr.todos.CardRow

@Composable
fun ColumnScope.Description(
    row: CardRow,
    style: CardStyle,
    onTagClick: (CardTag) -> Unit
) {
    when {
        row.content.value.text.isEmpty()
                && row.content.value.tags.isEmpty() -> EmptyText(row = row)
        else -> Text(row = row, style = style, onTagClick = onTagClick)
    }
}
