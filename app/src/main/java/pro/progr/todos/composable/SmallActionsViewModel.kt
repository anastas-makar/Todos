package pro.progr.todos.composable

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SmallActionsViewModel {
    var selectedDate: LocalDate? = null

    fun getActions() : List<SmallActionModel> {
        selectedDate?.let { localDate ->
            return getActionModels(localDate)
        } ?: run {
            return getActionModels()
        }
    }

    private fun getActionModels(): List<SmallActionModel> {
        return listOf(
            SmallActionModel(
                color = Color(0xff01579B),
                text = "Добавить задачу",
                destination = "createCard"
            ),
            SmallActionModel(
                color = Color(0xff006064),
                text = "Добавить задачу на сегодня",
                destination = "createCardForDate/${LocalDate.now().toEpochDay()}"
            ),
            SmallActionModel(
                color = Color(0xff4d143c),
                text = "Добавить задачу на завтра",
                destination = "createCardForDate/${LocalDate.now().plusDays(1L).toEpochDay()}"
            ),
        )
    }

    private fun getActionModels(selectedDate: LocalDate): List<SmallActionModel> {
        val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyy", Locale.getDefault())

        return listOf(
            SmallActionModel(
                color = Color(0xff4d143c),
                text = if (selectedDate.equals(LocalDate.now()))
                        "Добавить задачу на сегодня"
                else if (LocalDate.now().plusDays(1).equals(selectedDate))
                        "Добавить задачу на завтра"
                else
                    "+ на ${selectedDate.format(formatter)}",
                destination = "createCardForDate/${selectedDate.toEpochDay()}"
            ),
            SmallActionModel(
                color = Color(0xff01579B),
                text = "Добавить задачу",
                destination = "createCard"
            )
        )
    }
}