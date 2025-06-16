package pro.progr.doflow.composable.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pro.progr.doflow.NoteCalendarViewModel
import pro.progr.doflow.composable.*
import pro.progr.doflow.datefilters.FilterType

@Composable
fun SchedulePage(viewModel: NoteCalendarViewModel,
                 navController: NavHostController) {
    val selectedType by remember { viewModel.filterTypeState }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                ScheduleBar(navController)
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .navigationBarsPadding()
            ) {
                val isSequenceSelected = remember { mutableStateOf(false) }
                isSequenceSelected.value = selectedType in listOf(FilterType.EVERY_DAY, FilterType.DAYS_AFTER_DAYS, FilterType.DAY_AFTER_DAY)

                val isDatesFilterSelected = remember { mutableStateOf(false) }
                isDatesFilterSelected.value = selectedType in listOf(FilterType.DATE, FilterType.DATE_IN_YEAR, FilterType.DATE_IN_MONTH)

                Row(
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    TextButton(
                        onClick = { if (!isSequenceSelected.value) {
                            viewModel.selectFilterType(FilterType.EVERY_DAY)
                        } },
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isSequenceSelected.value) Color.LightGray else Color.Transparent),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = if (isSequenceSelected.value) Color.LightGray else MaterialTheme.colors.surface,
                            contentColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Повторять", color = MaterialTheme.colors.onSurface)
                    }
                    TextButton(
                        onClick = { viewModel.selectFilterType(FilterType.WEEK) },
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedType == FilterType.WEEK) Color.LightGray else Color.Transparent),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = if (selectedType == FilterType.WEEK) Color.LightGray else MaterialTheme.colors.surface,
                            contentColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Дни недели", color = MaterialTheme.colors.onSurface)
                    }
                    TextButton(
                        onClick = { viewModel.selectFilterType(FilterType.DATE) },
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isDatesFilterSelected.value) Color.LightGray else Color.Transparent),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = if (isDatesFilterSelected.value) Color.LightGray else MaterialTheme.colors.surface,
                            contentColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Даты", color = MaterialTheme.colors.onSurface)
                    }
                    TextButton(
                        onClick = { viewModel.selectFilterType(FilterType.NO_TERMS) },
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedType == FilterType.NO_TERMS) Color.LightGray else Color.Transparent),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = if (selectedType == FilterType.NO_TERMS) Color.LightGray else MaterialTheme.colors.surface,
                            contentColor = Color.Transparent
                        )
                    ) {
                        Text(text = FilterType.NO_TERMS.datesFilter.getTitle(), color = MaterialTheme.colors.onSurface)
                    }

                }
                Box(modifier = Modifier.weight(1f).padding(20.dp)) {
                    if (isSequenceSelected.value) {
                        SchedulePicker(calendarViewModel = viewModel)
                    } else if (selectedType == FilterType.WEEK) {
                        DaysOfWeekPanel(calendarViewModel = viewModel)
                    } else if (isDatesFilterSelected.value) {
                        DateSchedulePicker(calendarViewModel = viewModel)
                    } else if (selectedType == FilterType.NO_TERMS) {
                        Text("Задача не в календаре")
                    }

                }

                viewModel.blockTillToday = true
                viewModel.multiSelect = true
                if (selectedType != FilterType.NO_TERMS) {
                    BottomCalendar(viewModel)
                }
            }
        }
    )
}