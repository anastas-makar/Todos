package pro.progr.brightcards.vm

import pro.progr.brightcards.MenuItem
import pro.progr.brightcards.model.TodoStatus

class UndatedCardMenuItems(val todo : TodoStatus,
                           val updateDestination : (destination : Destinations) -> Unit) : ArrayList<MenuItem>() {

    init {
        markDone()
        doToday()
        doTomorrow()
        edit()
    }
    private fun edit() {
        add(
            MenuItem(
                "Редактировать",
                { updateDestination(Destinations.TO_EDIT_CARD) }
            )
        )
    }
    private fun markDone() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    "Пометить сделанным",
                    { updateDestination(Destinations.MARK_DONE) }
                )
            )

        }
    }
    private fun doToday() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    "Сделать сегодня",
                    { updateDestination(Destinations.DO_TODAY) }
                )
            )

        }
    }
    private fun doTomorrow() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    "Сделать завтра",
                    { updateDestination(Destinations.DO_TOMORROW) }
                )
            )

        }
    }
}