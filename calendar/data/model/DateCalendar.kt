package gleynuco.com.monpotager.features.calendar.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DateCalendar(
    val date: LocalDate,
    val isToday: Boolean
) {
    val day: String = date.format(DateTimeFormatter.ofPattern("E")) // get the day by formatting the date
}
