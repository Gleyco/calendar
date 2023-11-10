package gleynuco.com.monpotager.features.calendar.domain

import java.time.LocalDateTime

data class EventDisplayModel(
    val id : Long? = null,
    val name: String? = null,
    val color: String = "#66AEE9",
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
)
