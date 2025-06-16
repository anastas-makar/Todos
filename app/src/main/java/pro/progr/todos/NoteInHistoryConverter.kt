package pro.progr.doflow

import pro.progr.brightcards.MenuItem
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.model.CardTag
import pro.progr.brightcards.model.StringAnnotation
import pro.progr.doflow.db.NoteInHistory
import pro.progr.doflow.db.NoteInHistoryWithData
import pro.progr.doflow.db.NoteTag

class NoteInHistoryConverter {
    companion object {

        fun toCardContent(note: NoteInHistoryWithData, menuItems: List<MenuItem> = emptyList()) : CardContent {

            return CardContent(
                id = note.noteInHistory.noteId.toString(),
                style = note.noteInHistory.style,
                title = note.noteInHistory.title,
                text = note.noteInHistory.description,
                fillTitleBackground = note.noteInHistory.fillTitleBackground,
                fillTextBackground = note.noteInHistory.fillTextBackground,
                todo = note.noteInHistory.todo,
                reward = note.noteInHistory.reward,
                stringAnnotations = listOf(StringAnnotation("schedule", note.noteInHistory.schedule.pattern.type.datesFilter.getDescription(note.noteInHistory.schedule))),
                menuItems = menuItems,
                tags = note.tags.map { noteTag: NoteTag ->  CardTag(noteTag.id, noteTag.title)}
            )
        }

        fun toCardContent(note: NoteInHistory) : CardContent {

            return CardContent(
                id = note.noteId.toString(),
                style = note.style,
                title = note.title,
                text = note.description,
                fillTitleBackground = note.fillTitleBackground,
                fillTextBackground = note.fillTextBackground,
                todo = note.todo,
                reward = note.reward
            )
        }

    }
}