package gleynuco.com.monpotager.features.calendar.data.localdatasource


import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.data.CalendarUtils.toEventDisplayGroupModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayGroupModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import gleynuco.com.monpotager.features.calendar.domain.toEventDisplayModel
import gleynuco.com.monpotager.features.calendar.domain.toEventLocalDataSourceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CalendarEventRepository @Inject constructor(
    private val calendarEventDao: CalendarLocalDataSourceDao
){

    suspend fun addNewEvent (eventDisplayModel: EventDisplayModel) = withContext(Dispatchers.IO){

        calendarEventDao.insertEvent(eventDisplayModel.toEventLocalDataSourceModel())

    }

    suspend fun updateEvent (eventDisplayModel: EventDisplayModel) = withContext(Dispatchers.IO){
        calendarEventDao.updateEvent(eventDisplayModel.toEventLocalDataSourceModel())
    }

    suspend fun deleteEvent (id : Long) = withContext(Dispatchers.IO){
        calendarEventDao.deleteEventById(id)
    }




    fun getEventsForMonthGroupedByDay(newDate : LocalDate): Flow<Map<LocalDate, List<EventDisplayGroupModel>>> {

        // Specify the time zone (in this case, UTC)
        val zoneId = ZoneId.of("UTC")

        // Convert LocalDateTime to Instant in UTC
        val startMonth = LocalDateTime.of(newDate.year, newDate.month, 1,0,0).atZone(zoneId).toInstant()
        val endMonth = LocalDateTime.of(newDate.year, newDate.month, CalendarUtils.getNumberOfDayInThisMonth(newDate),0,0).atZone(zoneId).toInstant()


        return calendarEventDao.getEventsForInstantRange(startMonth.toString(), endMonth.toString())
            .map { events ->

                val eventsByDay = mutableMapOf<LocalDate, MutableList<EventDisplayModel>>()
                val zone = ZoneId.systemDefault()

                for (event in events) {

                    val day = Instant.parse(event.startDateTimeInstant).atZone(zone).toLocalDate()
                    if (!eventsByDay.containsKey(day)) {
                        eventsByDay[day] = mutableListOf()
                    }

                    eventsByDay[day]?.add(event.toEventDisplayModel())
                }

                val groupEventsByDay = mutableMapOf<LocalDate, List<EventDisplayGroupModel>>()

                eventsByDay.forEach {
                    if (!groupEventsByDay.containsKey(it.key)) {
                        groupEventsByDay[it.key] = mutableListOf()
                    }

                    groupEventsByDay[it.key] = it.value.toEventDisplayGroupModel()
                }

                groupEventsByDay
            }
    }
}