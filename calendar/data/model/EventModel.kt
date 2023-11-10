package gleynuco.com.monpotager.features.calendar.data.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class EventModel(
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
)
