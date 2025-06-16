package pro.progr.doflow

import pro.progr.brightcards.MenuItem
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.model.CardTag
import pro.progr.brightcards.model.StringAnnotation
import pro.progr.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.doflow.datefilters.FilterType
import pro.progr.doflow.db.Note
import pro.progr.doflow.db.NoteInHistory
import pro.progr.doflow.db.NoteWithData
import java.time.LocalDate
import java.util.*
import kotlin.collections.LinkedHashMap

class NoteConverter {
    companion object {
        fun toNote(
            cardContent: CardContent,
            schedule: Schedule = Schedule(Schedule.Pattern(LinkedHashMap(0), FilterType.NO_TERMS))) : Note {
            val note = Note(
                date = Date().time,
                title = cardContent.title,
                description = cardContent.text,
                style = cardContent.style,
                fillTextBackground = cardContent.fillTextBackground,
                fillTitleBackground = cardContent.fillTitleBackground,
                todo = cardContent.todo,
                reward = cardContent.reward,
                latestDone = cardContent.latestDone
            )

            note.schedule = schedule

            cardContent.id?.let {
                note.id = it.toLong()
            }

            return note
        }

        fun toCardContent(note: NoteWithData,
                          listName: String = "Список",
                          menuItems: List<MenuItem> = listOf()) : CardContent {

            //todo: Эту всю логику нужно в filtertype куда-то
            var datesExpired = false
            note.note.schedule.dates?.addedDates?.let {addedDates ->
                datesExpired = note.note.schedule.pattern.type == FilterType.DATE
                        && addedDates.size > 0
                        && addedDates.max() < LocalDate.now().toEpochDay()
            }

            return CardContent(
                id = note.note.id.toString(),
                style = note.note.style,
                title = note.note.title,
                text = note.note.description,
                fillTitleBackground = note.note.fillTitleBackground,
                fillTextBackground = note.note.fillTextBackground,

                todo = if (datesExpired) TodoStatus.DONE else note.note.todo,
                reward = note.note.reward,
                stringAnnotations = listOf(
                    StringAnnotation("schedule", note.note.schedule.pattern.type.datesFilter.getDescription(note.note.schedule)),
                    StringAnnotation("list", listName)
                ),
                latestDone = note.note.latestDone,
                menuItems = menuItems,
                tags = note.tags.map { noteTag -> CardTag(noteTag.id, noteTag.title) }
            )
        }

        fun noteEqualsCard(note: Note, cardContent: CardContent) : Boolean {
            return cardContent.id == note.id.toString() &&
            cardContent.style == note.style &&
            cardContent.title == note.title &&
            cardContent.text == note.description &&
            cardContent.fillTitleBackground == note.fillTitleBackground &&
            cardContent.fillTextBackground == note.fillTextBackground &&
            cardContent.todo == note.todo &&
            cardContent.reward == note.reward &&
            cardContent.latestDone == note.latestDone
        }

        fun toNoteInHistory(note: Note, date: Long) : NoteInHistory {
            return NoteInHistory(
                noteId = note.id,
                date = date,
                description = note.description,
                title = note.title,
                reward = note.reward,
                sublistChain = note.sublistChain,
                schedule = note.schedule,
                style = note.style,
                fillTitleBackground = note.fillTitleBackground,
                fillTextBackground = note.fillTextBackground,
                todo = note.todo
            )
        }

    }
}