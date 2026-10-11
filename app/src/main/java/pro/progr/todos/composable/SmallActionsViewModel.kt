package pro.progr.todos.composable

import android.content.Context
import pro.progr.todos.R

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SmallActionsViewModel {
    var selectedDate: LocalDate? = null

    fun getActions(context: Context) : List<SmallActionModel> {
        selectedDate?.let { localDate ->
            return getActionModels(localDate, context)
        } ?: run {
            return getActionModels(context)
        }
    }

    private fun getActionModels(context: Context): List<SmallActionModel> {
        return listOf(
            SmallActionModel(
                color = Color(0xff01579B),
                text = context.getString(R.string.add_task),
                destination = "createCard"
            ),
            SmallActionModel(
                color = Color(0xff006064),
                text = context.getString(R.string.add_task_today),
                destination = "createCardForDate/${LocalDate.now().toEpochDay()}"
            ),
            SmallActionModel(
                color = Color(0xff4d143c),
                text = context.getString(R.string.add_task_tomorrow),
                destination = "createCardForDate/${LocalDate.now().plusDays(1L).toEpochDay()}"
            ),
        )
    }

    private fun getActionModels(selectedDate: LocalDate, context: Context): List<SmallActionModel> {
        val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyy", Locale.getDefault())

        return listOf(
            SmallActionModel(
                color = Color(0xff4d143c),
                text = if (selectedDate.equals(LocalDate.now()))
                        context.getString(R.string.add_task_today)
                else if (LocalDate.now().plusDays(1).equals(selectedDate))
                        context.getString(R.string.add_task_tomorrow)
                else
                    context.getString(R.string.add_task_for_date, selectedDate.format(formatter)),
                destination = "createCardForDate/${selectedDate.toEpochDay()}"
            ),
            SmallActionModel(
                color = Color(0xff01579B),
                text = context.getString(R.string.add_task),
                destination = "createCard"
            )
        )
    }
}
