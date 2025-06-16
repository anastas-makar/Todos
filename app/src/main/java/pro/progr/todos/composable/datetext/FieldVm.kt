package pro.progr.todos.composable.datetext

import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate

class FieldVm(val pattern: String, date: LocalDate = LocalDate.now()) {
    val dateState = mutableStateOf(date)
}