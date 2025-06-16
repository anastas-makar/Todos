package pro.progr.todos.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import pro.progr.todos.NoteCalendarViewModel
import pro.progr.todos.datefilters.FilterType

@Composable
fun DateSchedulePicker(calendarViewModel : NoteCalendarViewModel) {
    val tabs = listOf(FilterType.DATE, FilterType.DATE_IN_YEAR, FilterType.DATE_IN_MONTH)
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val initialFilterType = calendarViewModel.getFilterType()

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

    }
}
