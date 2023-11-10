package gleynuco.com.monpotager.features.calendar.presentation

import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayDialogModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayGroupModel
import gleynuco.com.monpotager.utils.Constants
import java.time.LocalDate

data class CalendarState  constructor(
    val title : String = "",

    val isLoadingEvents : Boolean = false,

    val currentSelectedDate : LocalDate = LocalDate.now(),
    val dayEvents : List<EventDisplayGroupModel> = emptyList(),

    val mapEvents : Map<LocalDate, List<EventDisplayGroupModel>> = emptyMap(),

    val displayEventDialog : EventDisplayDialogModel? = null,


    val indexPagerDay : Int = CalendarUtils.daysSinceEpoch(LocalDate.now()),
    val indexPagerMonth : Int = CalendarUtils.monthsSinceEpoch(LocalDate.now()),


    val isCalendarExpanded : Boolean = true,
    val isCanScrollDayPager : Boolean = false,
    val moonPhase : Int = Constants.MOON_NEW
)
