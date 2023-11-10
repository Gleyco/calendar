package gleynuco.com.monpotager.features.calendar.presentation

import gleynuco.com.monpotager.features.calendar.domain.EventDisplayDialogModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import java.time.LocalDate

sealed interface CalendarScreenEvent{

    data class OnChangeCurrentDate (val newDate : LocalDate) : CalendarScreenEvent

    data class OnChangeExpandedCalendar (val isExpanded : Boolean) : CalendarScreenEvent

    data object OnCreateNewEvent : CalendarScreenEvent
    data class OnOpenEventDialog (val data : EventDisplayModel) : CalendarScreenEvent
    data object OnDismissEventDialog : CalendarScreenEvent
    data class OnSaveEventDialog (val data : EventDisplayModel) : CalendarScreenEvent
    data class OnDeleteEventDialog (val data : EventDisplayModel) : CalendarScreenEvent
    data class OnModifyEventDialogData (val data : EventDisplayDialogModel) : CalendarScreenEvent

}