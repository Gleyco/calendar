package gleynuco.com.monpotager.features.calendar.domain

import java.time.LocalDate

data class EventsInDayDisplayModel(
    val day : LocalDate,
    val events : List<EventDisplayGroupModel> = emptyList()
)
