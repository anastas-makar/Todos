package pro.progr.todos.brightcards.vm

import android.content.Context
import pro.progr.todos.R
import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.model.TodoStatus

class UndatedCardMenuItems(context: Context, val todo : TodoStatus,
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
                context.getString(R.string.edit),
                { updateDestination(Destinations.TO_EDIT_CARD) }
            )
        )
    }
    private fun markDone() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    context.getString(R.string.mark_done),
                    { updateDestination(Destinations.MARK_DONE) }
                )
            )

        }
    }
    private fun doToday() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    context.getString(R.string.make_today),
                    { updateDestination(Destinations.DO_TODAY) }
                )
            )

        }
    }
    private fun doTomorrow() {
        if (todo == TodoStatus.TODO) {
            add(
                MenuItem(
                    context.getString(R.string.make_tomorrow),
                    { updateDestination(Destinations.DO_TOMORROW) }
                )
            )

        }
    }
}
