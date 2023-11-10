package gleynuco.com.monpotager.features.calendar.data

import gleynuco.com.monpotager.features.calendar.domain.EventDisplayGroupModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.temporal.ChronoUnit

object CalendarUtils {

    fun monthsSinceEpoch(currentDate: LocalDate): Int {
        val epochDate = LocalDate.of(1970, Month.JANUARY, 1)

        return (currentDate.year - epochDate.year) * 12 + currentDate.monthValue - 1
    }

    fun monthsInMaxIntDays(): Int {
        val daysInYear = 365.0
        val averageDaysInMonth = daysInYear / 12.0
        val maxIntDays = Int.MAX_VALUE.toDouble()

        return (maxIntDays / averageDaysInMonth).toInt()
    }

    fun getLocalDateFromMonthsSinceEpoch(monthsSinceEpoch: Int): LocalDate {
        val epochDate = LocalDate.of(1970, Month.JANUARY, 1)

        return epochDate.plusMonths(monthsSinceEpoch.toLong())
    }

    fun daysSinceEpoch(date: LocalDate): Int {
        val epochDate = LocalDate.of(1970, Month.JANUARY, 1)
        return ChronoUnit.DAYS.between(epochDate, date).toInt()
    }

    fun localDateFromDaysSinceEpoch(daysSinceEpoch: Int): LocalDate {
        val epochDate = LocalDate.of(1970, Month.JANUARY, 1)
        return epochDate.plusDays(daysSinceEpoch.toLong())
    }


    fun List<EventDisplayModel>.toEventDisplayGroupModel () : List<EventDisplayGroupModel> {
        var newEventDisplayGroupModel : EventDisplayGroupModel? = null
        val listToReturn = mutableListOf<EventDisplayGroupModel>()

        this.sortedBy { it.start }
            .forEach {
                if (newEventDisplayGroupModel == null){
                    newEventDisplayGroupModel = EventDisplayGroupModel(
                        startGroup = it.start,
                        endGroup = it.end,
                        events = listOf(
                            it
                        )
                    )

                }else{
                    newEventDisplayGroupModel?.let { group ->
                        if (it.start >= group.startGroup
                            && it.start <= group.endGroup
                        ){
                            val list = group.events.toMutableList()
                            list.add(it)
                            newEventDisplayGroupModel = group.copy(
                                endGroup = if (it.end > group.endGroup) it.end else group.endGroup,
                                events = list
                            )

                        }else{

                            listToReturn.add(group)

                            newEventDisplayGroupModel = EventDisplayGroupModel(
                                startGroup = it.start,
                                endGroup = it.end,
                                events = listOf(
                                    it
                                )
                            )
                        }
                    }

                }

            }

        newEventDisplayGroupModel?.let {
            listToReturn.add(it)
        }


        return listToReturn
    }

    fun getNumberOfDayInThisMonth (date : LocalDate) : Int{
        // Calculate the number of days in the month
        return Year.of(date.year).atMonth(date.month).lengthOfMonth()
    }
}