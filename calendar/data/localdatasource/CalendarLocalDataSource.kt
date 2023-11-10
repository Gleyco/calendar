package gleynuco.com.monpotager.features.calendar.data.localdatasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


private const val DB_NAME = "calendar.db"

@Database(entities = [EventLocalDataSourceModel::class], version = 1, exportSchema = false)
abstract class CalendarLocalDataSource : RoomDatabase() {

    abstract fun calendarDao(): CalendarLocalDataSourceDao


    companion object {

        @Volatile
        private var INSTANCE: CalendarLocalDataSource? = null

        fun getInstance(context: Context): CalendarLocalDataSource =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                CalendarLocalDataSource::class.java,
                DB_NAME
            ).build()


        const val TABLE_CALENDAR = "table_calendar"

        const val COL_ID = "ID"
        const val COL_NAME = "name"
        const val COL_START_DATETIME = "start_datetime"
        const val COL_END_DATETIME = "end_datetime"
        const val COL_COLOR = "color"
        const val COL_DESCRIPTION = "description"



    }
}