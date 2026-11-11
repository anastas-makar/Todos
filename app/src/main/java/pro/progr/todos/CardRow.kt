package pro.progr.todos

import androidx.compose.runtime.MutableState
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardRowKey
import pro.progr.todos.db.NoteWithData

/** One stable occurrence of a note in a particular UI list/calendar position. */
data class CardRow(
    val key: CardRowKey,
    val note: NoteWithData,
    val content: MutableState<CardContent>
)
