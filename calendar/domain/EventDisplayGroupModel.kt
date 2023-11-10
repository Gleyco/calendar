package gleynuco.com.monpotager.features.calendar.domain

import java.time.LocalDateTime

data class EventDisplayGroupModel(
    val startGroup : LocalDateTime,
    val endGroup : LocalDateTime,
    val events : List<EventDisplayModel>
)
