package pro.progr.todos.brightcards.vm

import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.TodoStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CardMenuItems(val date: LocalDate,
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
                    "Не делать сегодня",
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
                    "Удалить эту запись",
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
                    "Убать заметку на сегодня",
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
                    "Пометить сделанным",
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
                    "Пометить сделанным",
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
                    "Пометить несделанным",
                    { updateDestination(Destinations.MARK_NOT_DONE) }
                )
            )
        }
    }

    private fun edit() {
        if (!date.isBefore(LocalDate.now())) {
            add(
                MenuItem(
                    "Редактировать для будущего",
                    { updateDestination(Destinations.TO_EDIT_CARD) }
                )
            )

        }
    }

    private fun editThis() {
        if (!date.isAfter(LocalDate.now())) {
            add(
                MenuItem(
                    "Редактировать для " +
                            if (date.equals(LocalDate.now()))
                                "сегодня"
                            else
                                date.format(formatter),
                    { updateDestination(Destinations.TO_EDIT_NOTE_IN_HISTORY) }
                )
            )

        }
    }
}