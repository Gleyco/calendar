package gleynuco.com.monpotager.features.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarEventRepository
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayDialogModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import gleynuco.com.monpotager.features.moon.data.MoonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject



@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarEventRepository: CalendarEventRepository

) : ViewModel() {
    private val _eventMonthFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val _eventMonthAndYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    private val todayLocalDate = LocalDate.now()
    private var _previousLocalDate : LocalDate? = null

    private var _scopeEvent : Job? = null

    private val _state = MutableStateFlow(CalendarState())
    val state = _state.stateIn(scope = viewModelScope, initialValue = CalendarState(), started = SharingStarted.WhileSubscribed(5000))

    init {
        modifyDataOnChangeSelectedDate(todayLocalDate)
    }


    fun onEvent (event: CalendarScreenEvent) {
        when (event) {
            is CalendarScreenEvent.OnChangeCurrentDate -> modifyDataOnChangeSelectedDate(event.newDate)

            is CalendarScreenEvent.OnChangeExpandedCalendar -> {
                _state.update { state -> state.copy(isCalendarExpanded = event.isExpanded) }
            }

            CalendarScreenEvent.OnCreateNewEvent -> {
                _state.update { state ->
                    state.copy(
                        displayEventDialog = EventDisplayDialogModel(
                            isEditMode = true,
                            event = EventDisplayModel(
                                start = state.currentSelectedDate.atTime(12, 0),
                                end = state.currentSelectedDate.atTime(12, 30)
                            )
                        )
                    )
                }
            }

            is CalendarScreenEvent.OnModifyEventDialogData -> {
                _state.update { state -> state.copy(displayEventDialog = event.data) }
            }

            CalendarScreenEvent.OnDismissEventDialog -> _state.update { state -> state.copy(displayEventDialog = null) }
            is CalendarScreenEvent.OnSaveEventDialog -> {
                _state.update { state -> state.copy(displayEventDialog = null) }
                viewModelScope.launch {
                    if (event.data.id == null){
                        addNewEvent(event.data)
                    }else{
                        updateEvent(event.data)
                    }
                }
            }

            is CalendarScreenEvent.OnOpenEventDialog -> {
                _state.update { state ->
                    state.copy(
                        displayEventDialog = EventDisplayDialogModel(
                            isEditMode = false,
                            event = event.data
                        )
                    )
                }
            }

            is CalendarScreenEvent.OnDeleteEventDialog -> {
                _state.update { state -> state.copy(displayEventDialog = null) }
                viewModelScope.launch {
                    deleteEvent(event.data)
                }
            }
        }
    }



    private fun modifyDataOnChangeSelectedDate (newDate : LocalDate){

        _state.update { state ->
            state.copy(
                currentSelectedDate = newDate,
                title =  if (newDate.year == todayLocalDate.year ){
                    newDate.format(_eventMonthFormatter)
                }else{
                    newDate.format(_eventMonthAndYearFormatter)
                },
                indexPagerDay = CalendarUtils.daysSinceEpoch(newDate),
                indexPagerMonth = CalendarUtils.monthsSinceEpoch(newDate),
                moonPhase = MoonUtils.calculateMoonPhase(newDate)
            )
        }

        if (newDate.year != _previousLocalDate?.year || newDate.month != _previousLocalDate?.month){
            _previousLocalDate = newDate
            getEventsOfSelectedDate(newDate)
        }
    }

    private fun getEventsOfSelectedDate (newDate : LocalDate){
        _scopeEvent?.run {
            cancel()
            _state.update { state -> state.copy(isLoadingEvents = false) }
            null
        }

        _scopeEvent = viewModelScope.launch {
            _state.update { state -> state.copy(isLoadingEvents = true) }
            flowEventsOfDay(newDate)
        }
    }

    private suspend fun flowEventsOfDay(newDate : LocalDate) = withContext(Dispatchers.IO){

        calendarEventRepository.getEventsForMonthGroupedByDay(newDate)
            .catch {
                println("catch error $it")
                _state.update { state -> state.copy(isLoadingEvents = false) }
                _previousLocalDate = null
            }
            .collectLatest {

                _state.update { state -> state.copy(
                    mapEvents = it,
                    isLoadingEvents = false
                )}
            }
    }


    private suspend fun addNewEvent (event : EventDisplayModel) = withContext(Dispatchers.IO){
        calendarEventRepository.addNewEvent(event)
    }

    private suspend fun updateEvent (event : EventDisplayModel) = withContext(Dispatchers.IO){
        calendarEventRepository.updateEvent(event)
    }

    private suspend fun deleteEvent (event : EventDisplayModel) = withContext(Dispatchers.IO){
        event.id?.let {
            calendarEventRepository.deleteEvent(it)
        }
    }
}



