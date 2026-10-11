package pro.progr.todos.brightcards.vm

import android.content.Context
import pro.progr.todos.R
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.TodoStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CardMenuItems(private val context: Context, val date: LocalDate,
                    val todo : TodoStatus,
                    val updateDestination : (destination : Destinations) -> Unit) : ArrayList<MenuItem>() {

    val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())

    init {
        notToday()
        setDoneMenuItem()
        setNoteInHistoryDone()
        setNotDoneMenuItem()
        removeForToday()
        edit()
        editThis()
        deleteNoteForThisDate()
    }

    private fun notToday() {
        if (date.equals(LocalDate.now())
            && todo == TodoStatus.TODO
        ) {
            add(
                MenuItem(
                    context.getString(R.string.remove_today),
                    {
                        updateDestination(Destinations.REMOVE_FOR_DAY)
                    }
                )
            )
        }
    }

    private fun deleteNoteForThisDate() {

        if (date.isBefore(LocalDate.now())
            || (date == LocalDate.now() && todo == TodoStatus.DONE)) {
            add(
                MenuItem(
                    context.getString(R.string.delete_entry),
                    {
                        updateDestination(Destinations.REMOVE_FOR_DAY)
                    }
                )
            )
        }
    }

    private fun removeForToday() {
        if (date.equals(LocalDate.now()) && todo == TodoStatus.NOT_ACTIVE) {
            add(
                MenuItem(
                    context.getString(R.string.remove_note_today),
                    {
                        updateDestination(Destinations.REMOVE_FOR_DAY)
                    }
                )
            )
        }
    }

    private fun setDoneMenuItem() {
        if (todo == TodoStatus.TODO
            && !date.isAfter(LocalDate.now())
        ) {
            add(
                MenuItem(
                    context.getString(R.string.mark_done),
                    { updateDestination(Destinations.MARK_DONE) }
                )
            )
        }
    }

    private fun setNoteInHistoryDone() {
        if (todo == TodoStatus.FAIL
            && date.isBefore(LocalDate.now())) {
            add(
                MenuItem(
                    context.getString(R.string.mark_done),
                    { updateDestination(Destinations.MARK_NOTE_IN_HISTORY_DONE) }
                )
            )
        }
    }

    private fun setNotDoneMenuItem() {
        if (todo == TodoStatus.DONE
            && date == LocalDate.now()
        ) {
            add(
                MenuItem(
                    context.getString(R.string.mark_not_done),
                    { updateDestination(Destinations.MARK_NOT_DONE) }
                )
            )
        }
    }

    private fun edit() {
        if (!date.isBefore(LocalDate.now())) {
            add(
                MenuItem(
                    context.getString(R.string.edit_template),
                    { updateDestination(Destinations.TO_EDIT_CARD) }
                )
            )

        }
    }

    private fun editThis() {
        if (!date.isAfter(LocalDate.now())) {
            add(
                MenuItem(
                    context.getString(
                        R.string.change_entry_date,
                        if (date.equals(LocalDate.now())) context.getString(R.string.today) else date.format(formatter)
                    ),
                    { updateDestination(Destinations.TO_EDIT_NOTE_IN_HISTORY) }
                )
            )

        }
    }
}
