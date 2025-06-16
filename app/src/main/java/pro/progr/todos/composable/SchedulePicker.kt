package pro.progr.todos.composable

import SinceTillElement
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.progr.todos.NoteCalendarViewModel
import pro.progr.todos.datefilters.FilterType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchedulePicker(calendarViewModel : NoteCalendarViewModel) {
    val tabs = listOf(FilterType.EVERY_DAY, FilterType.DAY_AFTER_DAY, FilterType.DAYS_AFTER_DAYS)
    var selectedTabIndex by remember { mutableStateOf(0) }

    var executeDays by remember { mutableStateOf(TextFieldValue(text = "1")) }
    var skipDays by remember { mutableStateOf(TextFieldValue(text = "1")) }

    LaunchedEffect(Unit) {
        val initialFilterType = calendarViewModel.getFilterType()

        if (initialFilterType == FilterType.DAYS_AFTER_DAYS) {
            calendarViewModel.getSchedule()?.pattern?.days?.let {days ->
                for (day in days) {
                    if (day.value.active) {
                        executeDays = TextFieldValue(day.value.longValue.toString())
                    } else {
                        skipDays = TextFieldValue(day.value.longValue.toString())
                    }
                }
            }

        }

        selectedTabIndex = tabs.indexOf(initialFilterType)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent) {
            tabs.forEachIndexed { index, filterType ->
                Tab(
                    text = { Text(filterType.datesFilter.getTitle()) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        calendarViewModel.selectFilterType(filterType)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> {
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Выполнять каждый день с", fontSize = 16.sp)
                    SinceTillElement(calendarViewModel)

                }
            }
            1 -> {

                FlowRow (
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Выполнять через день", fontSize = 16.sp)
                    SinceTillElement(calendarViewModel)
                }
            }

            2 -> {
                if (executeDays.text.isNotBlank() && skipDays.text.isNotBlank()) {

                    try {
                        if (calendarViewModel.getSchedule()?.pattern?.type == FilterType.DAYS_AFTER_DAYS)
                            calendarViewModel.setSequence(executeDays = executeDays.text.toLong(), skipDays = skipDays.text.toLong())

                    } catch (someWTF : NumberFormatException) {
                        Log.wtf("какой-то рассинхрон", "executeDays ${executeDays.text} skipDays ${skipDays.text}")
                    }
                }



                FlowRow (
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Выполнять", fontSize = 16.sp)
                    CustomTextField(executeDays, onValueChange = { executeDays = it })
                    Text("через", fontSize = 16.sp)
                    CustomTextField(skipDays, onValueChange = { skipDays = it })

                    SinceTillElement(calendarViewModel)

                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .background(Color.Transparent)
            .width(40.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = true,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    )
}
