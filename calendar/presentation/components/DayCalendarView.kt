package gleynuco.com.monpotager.features.calendar.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.data.model.EventModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayGroupModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayCalendarView (
    mapEvent : Map<LocalDate, List<EventDisplayGroupModel>>,
    onClickEvent : (EventDisplayModel) -> Unit,
    onClickNavigateMoon : (Long) -> Unit,
    isLoadingEvent : Boolean,
    verticalScrollState : ScrollState,
    pagerStateDay : PagerState,
    enableSwipeGesture : Boolean,
    isDayTopBarElevated : Boolean,
    moonPhase : Int,
    modifier : Modifier,
){

    HorizontalPager(
        state = pagerStateDay,
        userScrollEnabled = enableSwipeGesture,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) { page ->

        val date = remember { CalendarUtils.localDateFromDaysSinceEpoch(page)}

        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {

            DayTopBar(
                dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
                dayNumber = date.dayOfMonth.toString(),
                isElevated = isDayTopBarElevated,
                isLoadingEvent = isLoadingEvent,
                moonPhase = moonPhase,
                onClickNavigateMoon = {
                    val dateInMillis = date.atTime(LocalTime.now()).toInstant(OffsetTime.now().offset).toEpochMilli()
                    println("Moon date in millis start : $dateInMillis")
                    onClickNavigateMoon(dateInMillis)
                }
            )

            Schedule (
               // events = calendarEvents,
                events = mapEvent[date] ?: emptyList(),
                verticalScrollState = verticalScrollState,
                onClickEvent = onClickEvent,
                modifier = Modifier.weight(1f)
            )
        }
    }

}

@Composable
fun Schedule (
    events: List<EventDisplayGroupModel>,
    verticalScrollState : ScrollState,
    onClickEvent : (EventDisplayModel) -> Unit,
    hourHeight : Dp = 64.dp,
    modifier : Modifier = Modifier,
    eventContent: @Composable (event: EventDisplayGroupModel) -> Unit = { BasicEventView(eventGroup = it, onClickEvent = onClickEvent) },
){
    val colorSpacerHour = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    Row(
        modifier = modifier
    ) {
        ScheduleSidebar(
            hourHeight = hourHeight,
            modifier = Modifier
                .verticalScroll(verticalScrollState)
        )

        Layout(
            content = {
                events.forEach { event ->
                    Box(modifier = Modifier.eventData(event)) {
                        eventContent(
                            event
                        )
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .verticalScroll(verticalScrollState)
                .drawBehind {
                    repeat(23) {
                        drawLine(
                            color = colorSpacerHour,
                            start = Offset(0f, (it + 1) * hourHeight.toPx()),
                            end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                },
        ) { measureables, constraints ->

            val height = hourHeight.roundToPx() * 24
            val placeablesWithEvents = measureables.map { measurable ->
                val event = measurable.parentData as EventDisplayGroupModel
                val eventDurationMinutes = ChronoUnit.MINUTES.between(event.startGroup, event.endGroup)
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val placeable = measurable.measure(
                    constraints.copy(
                        minHeight = eventHeight,
                        maxHeight = eventHeight
                    )
                )
                Pair(placeable, event)
            }

            layout(constraints.maxWidth, height) {
                placeablesWithEvents.forEach { (placeable, event) ->
                    val eventOffsetMinutes =
                        ChronoUnit.MINUTES.between(LocalTime.MIN, event.startGroup.toLocalTime())
                    val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                    placeable.place(0, eventY)
                }
            }
        }
    }
}


@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {

    //Use to align the hours with the line spacer hours
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodySmall
    val textLayoutResult = remember { textMeasurer.measure("10:10", style) }

    Column(
        modifier = modifier
            .offset(x = 0.dp, y = -((with(LocalDensity.current) { (textLayoutResult.size.height/2).toDp() }) ))
    ) {
        val startTime = LocalTime.MIN
        repeat(24) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                if (i != 0){
                    label(startTime.plusHours(i.toLong()))
                }
            }
        }
    }
}

private val HourFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
    )
}


private class EventDataModifier(
    val event: EventDisplayGroupModel,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: EventDisplayGroupModel) = this.then(EventDataModifier(event))