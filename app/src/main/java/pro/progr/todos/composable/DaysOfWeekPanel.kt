package pro.progr.todos.composable

import SinceTillElement
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.progr.todos.NoteCalendarViewModel
import pro.progr.todos.composable.datetext.DateField
import pro.progr.todos.composable.datetext.FieldVm
import pro.progr.flow.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfWeekPanel(calendarViewModel: NoteCalendarViewModel) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            DayColumn(stringResource(id = R.string.monday), calendarViewModel.isDayOfWeekSelected(1), {
                calendarViewModel.toggleDayOfWeek(1)
            })
            DayColumn(stringResource(id = R.string.tuesday), calendarViewModel.isDayOfWeekSelected(2), {
                calendarViewModel.toggleDayOfWeek(2)
            })
            DayColumn(stringResource(id = R.string.wednesday), calendarViewModel.isDayOfWeekSelected(3), {
                calendarViewModel.toggleDayOfWeek(3)
            })
            DayColumn(stringResource(id = R.string.thursday), calendarViewModel.isDayOfWeekSelected(4), {
                calendarViewModel.toggleDayOfWeek(4)
            })
            DayColumn(stringResource(id = R.string.friday), calendarViewModel.isDayOfWeekSelected(5), {
                calendarViewModel.toggleDayOfWeek(5)
            })
            DayColumn(stringResource(id = R.string.saturday), calendarViewModel.isDayOfWeekSelected(6), {
                calendarViewModel.toggleDayOfWeek(6)
            }, Color.Red)
            DayColumn(stringResource(id = R.string.sunday), calendarViewModel.isDayOfWeekSelected(7), {
                calendarViewModel.toggleDayOfWeek(7)
            }, Color.Red)
        }

        FlowRow (
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Повторять", fontSize = 16.sp)
                SinceTillElement(calendarViewModel)
            }
        }

    }
}

@Composable
fun RowScope.DayColumn(
    day: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textColor: Color = Color.Unspecified
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day, color = textColor, textAlign = TextAlign.Center)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
