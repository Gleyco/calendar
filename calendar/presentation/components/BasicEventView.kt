package gleynuco.com.monpotager.features.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import gleynuco.com.monpotager.R
import gleynuco.com.monpotager.features.calendar.data.model.EventModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayGroupModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun BasicEventView(
    eventGroup : EventDisplayGroupModel,
    onClickEvent : (EventDisplayModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)
    ){
        eventGroup.events.forEach { event ->

            val spacersPercentage = calculateSpacerPercentage(
                    startTimeEvent = event.start,
                    endTimeEvent = event.end,
                    startTimeGroup = eventGroup.startGroup,
                    endTimeGroup = eventGroup.endGroup
                )


            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ){

                Spacer(modifier = Modifier.fillMaxHeight(spacersPercentage.first))

                EventView(
                    event = event,
                    onClickEvent = { onClickEvent(event) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.fillMaxHeight(spacersPercentage.second))
            }


        }
    }
}

@Composable
fun EventView(
    event : EventDisplayModel,
    onClickEvent : () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 2.dp)
            .background(
                Color(android.graphics.Color.parseColor(event.color)),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClickEvent() }
    ) {


        Text(
            text = event.name ?: stringResource(id = R.string.event_no_title),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

    }

}


private fun calculateSpacerPercentage(
    startTimeEvent: LocalDateTime,
    endTimeEvent: LocalDateTime,
    startTimeGroup: LocalDateTime,
    endTimeGroup: LocalDateTime
): Pair<Float, Float> {

    val totalSecond = endTimeGroup.toEpochSecond(ZoneOffset.UTC) - startTimeGroup.toEpochSecond(ZoneOffset.UTC)

    val differenceStartSecond = startTimeEvent.toEpochSecond(ZoneOffset.UTC)  - startTimeGroup.toEpochSecond(ZoneOffset.UTC)
    val differenceEndSecond = endTimeGroup.toEpochSecond(ZoneOffset.UTC)  - endTimeEvent.toEpochSecond(ZoneOffset.UTC)



    val startSpacerPercentage = (differenceStartSecond.toFloat() / totalSecond.toFloat())
    val endSpacerPercentage = (differenceEndSecond.toFloat() / totalSecond.toFloat())

    return Pair(startSpacerPercentage, endSpacerPercentage)
}