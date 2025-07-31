package pro.progr.todos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.datefilters.FilterType
import pro.progr.todos.db.ArrayPOJO
import pro.progr.todos.db.Note
import pro.progr.flow.vm.CalendarViewModel
import java.time.LocalDate
import javax.inject.Inject

class NoteCalendarViewModel @Inject constructor(
    var datesGridRepository: DatesGridRepository,
    id: String
) : CalendarViewModel(datesGridRepository) {

    private var _note: MutableState<Note?> = mutableStateOf(null)

    val filterTypeState: MutableState<FilterType> = mutableStateOf(FilterType.NO_TERMS)

    init {
        viewModelScope.launch {
            datesGridRepository.getNote(id).collectLatest { note ->

                note?.let {
                    if (_note.value == null) {
                        note.schedule.dates?.addedDates?.let { longDateVals ->
                            datesGridRepository.selectedDates.addAll(longDateVals.map { longDateVal ->
                                LocalDate.ofEpochDay(longDateVal)
                            })
                        }
                    }

                    _note.value = note
                    filterTypeState.value = note.schedule.pattern.type
                }
            }
        }
    }

    fun selectFilterType(type: FilterType) {
        _note.value?.let { note ->
            viewModelScope.launch(Dispatchers.Default) {
                if (note.schedule.pattern.type != type) {

                    note.schedule = Schedule(Schedule.Pattern(days = LinkedHashMap(), type = type))

                    datesGridRepository.updateNote(note)
                }
            }
        }
    }

    override fun selectGridDate(date: LocalDate) {
        super.selectGridDate(date)
        _note.value?.let { note ->
            viewModelScope.launch(Dispatchers.Default) {

                note.addedDates = ArrayPOJO(datesGridRepository.selectedDates.map { lDate ->
                    lDate.toEpochDay()
                }.toTypedArray())

                note.dateSince = if (datesGridRepository.selectedDates.size > 0)
                    datesGridRepository.selectedDates.min().toEpochDay()
                else
                    LocalDate.now().toEpochDay()


                note.dateTill = if (datesGridRepository.selectedDates.size > 0)
                        datesGridRepository.selectedDates.max().toEpochDay()
                    else
                        null

                note.schedule.dates?.addedDates?.let { addedDates ->
                    if (note.todo != TodoStatus.NOT_ACTIVE && addedDates.isNotEmpty()) {
                        val max = addedDates.max()
                        if (max > LocalDate.now().toEpochDay()
                            || (max == LocalDate.now()
                                .toEpochDay() && note.latestDone != LocalDate.now().toEpochDay())
                        ) {
                            note.todo = TodoStatus.TODO
                        }
                    }
                }

                datesGridRepository.updateNote(note)
            }
        }
    }

    override fun isDateSelected(date: LocalDate): Boolean {
        return _note.value?.let { note ->
            note.schedule.pattern.type.datesFilter.isActual(note.schedule, date)
        } ?: false
    }

    fun toggleDayOfWeek(dayNum: Long) {
        _note.value?.let { note ->

            if (note.schedule.pattern.days.containsKey(dayNum)) {
                cancelDayOfWeek(dayNum)
            } else {
                selectDayOfWeek(dayNum)
            }
        }
    }

    fun selectDayOfWeek(dayNum: Long) {
        _note.value?.let { note ->
            note.schedule.pattern.days.put(
                dayNum,
                Schedule.Day(dayNum, true)
            )

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun cancelDayOfWeek(dayNum: Long) {
        _note.value?.let { note ->
            note.schedule.pattern.days.remove(
                dayNum
            )

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun isDayOfWeekSelected(dayNum: Long): Boolean {
        return _note.value?.schedule?.pattern?.days?.containsKey(dayNum) ?: false
    }

    fun setSequence(executeDays: Long, skipDays: Long) {
        _note.value?.let { note ->

            if (note.patterDates?.get(0)?.longValue == executeDays
                && note.patterDates?.get(1)?.longValue == skipDays) {
                return
            }

            note.patterDates?.put(
                0,
                Schedule.Day(
                    executeDays,
                    true
                )
            )

            note.patterDates?.put(
                1,
                Schedule.Day(
                    skipDays,
                    false
                )
            )

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun setSinceTill(since: Long, till: Long) {
        _note.value?.let { note ->

            if (note.dateSince == since
                && note.dateTill == till) {
                return
            }

            note.dateSince = since

            note.dateTill = till

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun setOnlySince(since: Long) {
        _note.value?.let { note ->

            if (note.dateSince == since
                && note.dateTill == null) {
                return
            }

            note.dateSince = since

            note.dateTill = null

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun unsetTill() {
        _note.value?.let { note ->

            note.dateTill = null

            viewModelScope.launch(Dispatchers.Default) {
                datesGridRepository.updateNote(note)
            }
        }
    }

    fun getFilterType(): FilterType {
        return filterTypeState.value
    }

    fun getSchedule(): Schedule? {
        return _note.value?.schedule
    }
}