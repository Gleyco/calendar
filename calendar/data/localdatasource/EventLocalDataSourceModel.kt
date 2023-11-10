package gleynuco.com.monpotager.features.calendar.data.localdatasource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_COLOR
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_DESCRIPTION
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_END_DATETIME
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_ID
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_NAME
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.COL_START_DATETIME
import gleynuco.com.monpotager.features.calendar.data.localdatasource.CalendarLocalDataSource.Companion.TABLE_CALENDAR

@Entity(tableName = TABLE_CALENDAR)
data class EventLocalDataSourceModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name= COL_ID)
    var id: Long?,

    @ColumnInfo(name = COL_NAME)
    var name: String?,

    @ColumnInfo(name = COL_START_DATETIME)
    var startDateTimeInstant : String,

    @ColumnInfo(name = COL_END_DATETIME)
    var endDateTimeInstant: String,

    @ColumnInfo(name = COL_COLOR)
    var color: String,

    @ColumnInfo(name = COL_DESCRIPTION)
    var description : String?,


)
