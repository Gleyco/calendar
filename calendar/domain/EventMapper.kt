package gleynuco.com.monpotager.features.calendar.domain

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColor
import gleynuco.com.monpotager.features.calendar.data.localdatasource.EventLocalDataSourceModel
import java.time.Instant
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalStdlibApi::class)
fun EventLocalDataSourceModel.toEventDisplayModel (
) : EventDisplayModel {
    val zone = ZoneId.systemDefault()

    return EventDisplayModel(
        id = this.id,
        name = this.name,
        color = this.color  ,
        start = Instant.parse(this.startDateTimeInstant).atZone(zone).toLocalDateTime(),
        end = Instant.parse(this.endDateTimeInstant).atZone(zone).toLocalDateTime(),
        description = this.description
    )
}


fun EventDisplayModel.toEventLocalDataSourceModel (
) : EventLocalDataSourceModel {


    return EventLocalDataSourceModel(
        id = this.id,
        name = this.name,
        color = this.color ,
        startDateTimeInstant = this.start.toInstant(OffsetTime.now().offset).toString(),
        endDateTimeInstant = this.end.toInstant(OffsetTime.now().offset).toString(),
        description = this.description
    )
}