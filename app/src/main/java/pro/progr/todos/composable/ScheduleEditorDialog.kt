package pro.progr.todos.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.R
import pro.progr.todos.datefilters.FilterType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private enum class ScheduleMode { NONE, DATES, WEEKDAYS, REPEAT }

@Composable
fun ScheduleEditorDialog(
    initialSchedule: Schedule,
    onDismiss: () -> Unit,
    onApply: (Schedule) -> Unit
) {
    val initial = remember(initialSchedule) { initialSchedule.deepCopy() }
    var mode by remember(initial) { mutableStateOf(initial.mode()) }
    var type by remember(initial) { mutableStateOf(initial.pattern.type) }
    var selectedDates by remember(initial) {
        mutableStateOf((initial.dates?.addedDates ?: emptyArray()).toSet())
    }
    var selectedWeekdays by remember(initial) {
        mutableStateOf(initial.pattern.days.filterValues { it.active }.keys.toSet())
    }
    var executeDays by remember(initial) {
        mutableStateOf(initial.pattern.days[0]?.longValue?.toString() ?: "1")
    }
    var skipDays by remember(initial) {
        mutableStateOf(initial.pattern.days[1]?.longValue?.toString() ?: "1")
    }
    var sinceText by remember(initial) {
        mutableStateOf(initial.dates?.since?.let { LocalDate.ofEpochDay(it).toString() } ?: "")
    }
    var tillText by remember(initial) {
        mutableStateOf(initial.dates?.till?.let { LocalDate.ofEpochDay(it).toString() } ?: "")
    }
    var visibleMonth by remember(initial) {
        mutableStateOf(
            selectedDates.minOrNull()?.let { YearMonth.from(LocalDate.ofEpochDay(it)) }
                ?: YearMonth.now()
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            elevation = 12.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.schedule),
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(8.dp))

                ModeOption(stringResource(R.string.task_not_in_calendar), mode == ScheduleMode.NONE) {
                    mode = ScheduleMode.NONE
                    type = FilterType.NO_TERMS
                }
                ModeOption(stringResource(R.string.dates), mode == ScheduleMode.DATES) {
                    mode = ScheduleMode.DATES
                    if (type !in dateTypes) type = FilterType.DATE
                }
                ModeOption(stringResource(R.string.weekdays), mode == ScheduleMode.WEEKDAYS) {
                    mode = ScheduleMode.WEEKDAYS
                    if (type !in weekTypes) type = FilterType.WEEK
                }
                ModeOption(stringResource(R.string.repeat), mode == ScheduleMode.REPEAT) {
                    mode = ScheduleMode.REPEAT
                    if (type !in repeatTypes) type = FilterType.EVERY_DAY
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (mode) {
                        ScheduleMode.NONE -> Text(stringResource(R.string.task_not_in_calendar))
                        ScheduleMode.DATES -> {
                            TypeOptions(dateTypes, type) { type = it }
                            MonthCalendar(
                                month = visibleMonth,
                                selectedEpochDays = selectedDates,
                                onMonthChange = { visibleMonth = it },
                                onDateClick = { date ->
                                    val epochDay = date.toEpochDay()
                                    selectedDates = if (epochDay in selectedDates) {
                                        selectedDates - epochDay
                                    } else {
                                        selectedDates + epochDay
                                    }
                                }
                            )
                            Text(
                                text = if (selectedDates.isEmpty()) stringResource(R.string.no_dates_selected)
                                else stringResource(R.string.selected_dates_count, selectedDates.size),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        ScheduleMode.WEEKDAYS -> {
                            Text(stringResource(R.string.weekdays))
                            val days = listOf(
                                R.string.monday, R.string.tuesday, R.string.wednesday,
                                R.string.thursday, R.string.friday, R.string.saturday, R.string.sunday
                            )
                            days.forEachIndexed { index, label ->
                                val key = index + 1L
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = key in selectedWeekdays,
                                        onCheckedChange = {
                                            selectedWeekdays = if (key in selectedWeekdays) selectedWeekdays - key
                                            else selectedWeekdays + key
                                        }
                                    )
                                    Text(stringResource(label))
                                }
                            }
                            DateBounds(sinceText, tillText, { sinceText = it }, { tillText = it })
                        }
                        ScheduleMode.REPEAT -> {
                            TypeOptions(repeatTypes, type) { type = it }
                            if (type == FilterType.DAYS_AFTER_DAYS) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = executeDays,
                                    onValueChange = { executeDays = it.filter { char -> char.isDigit() } },
                                        label = { Text(stringResource(R.string.repeat)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = skipDays,
                                    onValueChange = { skipDays = it.filter { char -> char.isDigit() } },
                                        label = { Text(stringResource(R.string.through)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            DateBounds(sinceText, tillText, { sinceText = it }, { tillText = it })
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Button(
                        onClick = {
                            val parsedSince = sinceText.toEpochDayOrNull()
                            val parsedTill = tillText.toEpochDayOrNull()
                            val newSchedule = when (mode) {
                                ScheduleMode.NONE -> Schedule(
                                    pattern = Schedule.Pattern(linkedMapOf(), FilterType.NO_TERMS),
                                    dates = Schedule.Dates()
                                )
                                ScheduleMode.DATES -> {
                                    val dates = selectedDates.sorted().toTypedArray()
                                    Schedule(
                                        pattern = Schedule.Pattern(linkedMapOf(), type),
                                        dates = Schedule.Dates(
                                            since = dates.firstOrNull(),
                                            till = dates.lastOrNull(),
                                            addedDates = dates
                                        )
                                    )
                                }
                                ScheduleMode.WEEKDAYS -> Schedule(
                                    pattern = Schedule.Pattern(
                                        LinkedHashMap<Long, Schedule.Day>().apply {
                                            selectedWeekdays.sorted().forEach { put(it, Schedule.Day(it, true)) }
                                        },
                                        type
                                    ),
                                    dates = Schedule.Dates(since = parsedSince, till = parsedTill)
                                )
                                ScheduleMode.REPEAT -> {
                                    val days = when (type) {
                                        FilterType.DAYS_AFTER_DAYS -> linkedMapOf(
                                            0L to Schedule.Day(executeDays.toLongOrNull() ?: 1L, true),
                                            1L to Schedule.Day(skipDays.toLongOrNull() ?: 1L, false)
                                        )
                                        else -> linkedMapOf()
                                    }
                                    Schedule(
                                        pattern = Schedule.Pattern(days, type),
                                        dates = Schedule.Dates(since = parsedSince, till = parsedTill)
                                    )
                                }
                            }
                            onApply(newSchedule)
                        },
                        enabled = dateBoundsValid(sinceText, tillText)
                                && (mode != ScheduleMode.DATES || selectedDates.isNotEmpty())
                                && (type != FilterType.DAYS_AFTER_DAYS
                                    || ((executeDays.toLongOrNull() ?: 0L) > 0L
                                        && (skipDays.toLongOrNull() ?: 0L) > 0L)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text(stringResource(R.string.save)) }
                }
            }
        }
    }
}

@Composable
private fun ModeOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label)
    }
}

@Composable
private fun TypeOptions(types: List<FilterType>, selected: FilterType, onSelect: (FilterType) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column {
        types.forEach { filterType ->
            ModeOption(
                label = filterType.datesFilter.getTitle(context),
                selected = filterType == selected,
                onSelect = { onSelect(filterType) }
            )
        }
    }
}

@Composable
private fun DateBounds(
    since: String,
    till: String,
    onSinceChange: (String) -> Unit,
    onTillChange: (String) -> Unit
) {
    Text(stringResource(R.string.date_range_hint), modifier = Modifier.padding(top = 8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = since,
            onValueChange = onSinceChange,
            label = { Text(stringResource(R.string.from)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = till,
            onValueChange = onTillChange,
            label = { Text(stringResource(R.string.to)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MonthCalendar(
    month: YearMonth,
    selectedEpochDays: Set<Long>,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = { runCatching { onMonthChange(month.minusMonths(1)) } }) { Text("‹") }
        Text("${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}")
        TextButton(onClick = { runCatching { onMonthChange(month.plusMonths(1)) } }) { Text("›") }
    }
    val weekdays = (1..7).map { day ->
        DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .take(2)
    }
    Row(Modifier.fillMaxWidth()) {
        weekdays.forEach { day ->
            Text(day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.caption)
        }
    }
    val first = month.atDay(1)
    val startOffset = first.dayOfWeek.value - 1
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
    ) {
        items(42) { index ->
            val dayNumber = index - startOffset + 1
            if (dayNumber in 1..month.lengthOfMonth()) {
                val date = month.atDay(dayNumber)
                val selected = date.toEpochDay() in selectedEpochDays
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 3.dp)
                        .background(
                            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                            shape = CircleShape
                        )
                        .clickable(onClick = { onDateClick(date) })
                        .semantics { this.selected = selected },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayNumber.toString(),
                        color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )
                }
            } else {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

private val dateTypes = listOf(FilterType.DATE, FilterType.DATE_IN_YEAR, FilterType.DATE_IN_MONTH)
private val repeatTypes = listOf(FilterType.EVERY_DAY, FilterType.DAY_AFTER_DAY, FilterType.DAYS_AFTER_DAYS)
private val weekTypes = listOf(
    FilterType.WEEK,
    FilterType.WEEK_IN_MONTH,
    FilterType.WEEK_IN_YEAR,
    FilterType.WEEK_AFTER_WEEK
)

private fun Schedule.mode(): ScheduleMode = when (pattern.type) {
    FilterType.NO_TERMS -> ScheduleMode.NONE
    in dateTypes -> ScheduleMode.DATES
    in weekTypes -> ScheduleMode.WEEKDAYS
    else -> ScheduleMode.REPEAT
}

private fun Schedule.deepCopy(): Schedule = Schedule(
    pattern = Schedule.Pattern(
        LinkedHashMap(pattern.days),
        pattern.type
    ),
    dates = Schedule.Dates(
        since = dates?.since,
        till = dates?.till,
        cancelledDates = dates?.cancelledDates?.copyOf() ?: emptyArray(),
        addedDates = dates?.addedDates?.copyOf() ?: emptyArray()
    )
)

private fun String.toEpochDayOrNull(): Long? = runCatching {
    if (isBlank()) null else LocalDate.parse(this).toEpochDay()
}.getOrNull()

private fun dateBoundsValid(since: String, till: String): Boolean {
    val sinceDay = since.toEpochDayOrNull()
    val tillDay = till.toEpochDayOrNull()
    if (since.isNotBlank() && sinceDay == null) return false
    if (till.isNotBlank() && tillDay == null) return false
    return sinceDay == null || tillDay == null || sinceDay <= tillDay
}
