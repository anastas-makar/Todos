package pro.progr.todos

import android.content.Context
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.db.NoteDateEntry
import pro.progr.todos.db.NoteDateEntryWithData
import pro.progr.todos.db.NoteWithData

object NoteDateEntryConverter {
    fun toCardContent(entry: NoteDateEntryWithData, context: Context): CardContent {
        val template = NoteConverter.toCardContent(NoteWithData(entry.note, entry.tags), context)
        return template.copy(
            id = entry.dateEntry.noteId,
            title = entry.dateEntry.title,
            text = entry.dateEntry.description,
            style = entry.dateEntry.style,
            fillTitleBackground = entry.dateEntry.fillTitleBackground,
            fillTextBackground = entry.dateEntry.fillTextBackground,
            todo = entry.dateEntry.todo
        )
    }

    fun fromCardContent(content: CardContent, date: Long, deleted: Boolean = false): NoteDateEntry {
        val noteId = requireNotNull(content.id) { "A date entry must reference a Note" }
        return NoteDateEntry(
            id = "${noteId}_$date",
            noteId = noteId,
            date = date,
            title = content.title,
            description = content.text,
            style = content.style,
            fillTitleBackground = content.fillTitleBackground,
            fillTextBackground = content.fillTextBackground,
            todo = when (content.todo) {
                pro.progr.todos.brightcards.model.TodoStatus.TODO_NOT_ACTIVE,
                pro.progr.todos.brightcards.model.TodoStatus.FAIL -> pro.progr.todos.brightcards.model.TodoStatus.TODO
                else -> content.todo
            },
            deleted = deleted
        )
    }
}
