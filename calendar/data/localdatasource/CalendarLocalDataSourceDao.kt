package gleynuco.com.monpotager.features.calendar.data.localdatasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_ID
import kotlinx.coroutines.flow.Flow


@Dao
interface CalendarLocalDataSourceDao {

    @Insert
    suspend fun insertEvent(event : EventLocalDataSourceModel)

    @Update
    suspend fun updateEvent(event : EventLocalDataSourceModel)

    @Query("DELETE FROM table_calendar WHERE $COL_ID = :id")
    suspend fun deleteEventById(id: Long)


    @Query("SELECT * FROM table_calendar WHERE :queryStart <= start_datetime AND end_datetime <= :queryEnd")
    fun getEventsForInstantRange(queryStart: String, queryEnd: String): Flow<List<EventLocalDataSourceModel>>
}