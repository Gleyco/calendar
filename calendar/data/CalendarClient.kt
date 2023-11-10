package gleynuco.com.monpotager.features.calendar.data


import gleynuco.com.monpotager.features.calendar.data.model.DateCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarClient {

    private val today = LocalDate.now()


    fun getDaysOfMonth (selectedDate : LocalDate) : List<DateCalendar?>{

        val daysInMonth = selectedDate.month.length(selectedDate.isLeapYear)
        val firstDayOfMonth = selectedDate.withDayOfMonth(1).dayOfWeek.value

        var numberDaysAddedBefore = 0

        return buildList {
            for (i in 1 until firstDayOfMonth) {
                add(null)
                numberDaysAddedBefore++
            }
            for (day in 1..daysInMonth) {
                //add(LocalDate.of(selectedDate.year, selectedDate.monthValue, day))

                add(
                    LocalDate
                        .of(selectedDate.year, selectedDate.monthValue, day)
                        .toUiModel(
                            today = today
                        )
                )
            }
            if ((7 - (numberDaysAddedBefore + daysInMonth)%7) != 7){
                for (i in 1 ..  (7 - (numberDaysAddedBefore + daysInMonth)%7)) {
                    add(null)
                }
            }

        }
    }
}

private fun LocalDate.toUiModel(
    today : LocalDate
): DateCalendar {
    return DateCalendar(
        date = this,
        isToday = this.isEqual(today)
    )
}


