import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.sp
import pro.progr.doflow.NoteCalendarViewModel
import pro.progr.doflow.composable.datetext.DateField
import pro.progr.doflow.composable.datetext.FieldVm
import java.time.LocalDate

@Composable
fun SinceTillElement(calendarViewModel: NoteCalendarViewModel) {
    val pattern = "dd-MM-yyyy"

    val startFieldVm = remember {
        calendarViewModel.getSchedule()?.dates?.since?.let {epochDay ->

            FieldVm(pattern, LocalDate.ofEpochDay(epochDay))

        }?: kotlin.run {
            FieldVm(pattern)
        }
    }
    val endFieldVm = remember {
        calendarViewModel.getSchedule()?.dates?.till?.let {epochDay ->

            FieldVm(pattern, LocalDate.ofEpochDay(epochDay))

        }?: kotlin.run {
            FieldVm(pattern)
        }
    }
    val showTill = remember {
        mutableStateOf(endFieldVm.dateState.value.isAfter(startFieldVm.dateState.value))
    }

    if (startFieldVm.dateState.value.isBefore(endFieldVm.dateState.value)) {
        calendarViewModel.setSinceTill(
            startFieldVm.dateState.value.toEpochDay(),
            endFieldVm.dateState.value.toEpochDay())
        calendarViewModel.updateSelectedDate(startFieldVm.dateState.value)
    } else if (startFieldVm.dateState.value.isAfter(LocalDate.now())) {
        calendarViewModel.setOnlySince(
            startFieldVm.dateState.value.toEpochDay())
        calendarViewModel.updateSelectedDate(startFieldVm.dateState.value)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("с", fontSize = 16.sp)
        DateField(pattern, startFieldVm)
        Text("по", fontSize = 16.sp)
        if (!showTill.value) {
            TextButton(onClick = { showTill.value = true }) {
                Text(text = "...")
            }

        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DateField(pattern, endFieldVm)
                IconButton(onClick = {
                    endFieldVm.dateState.value = LocalDate.now()
                    calendarViewModel.unsetTill()
                    showTill.value = false
                }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "не завершать")
                }

            }
        }

    }
}