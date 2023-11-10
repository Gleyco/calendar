package gleynuco.com.monpotager.features.calendar.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gleynuco.com.monpotager.features.calendar.data.CalendarClient
import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.data.model.DateCalendar
import gleynuco.com.monpotager.presentation.theme.MonpotagerTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthCalendarView (
    selectedDate: LocalDate,
    onChangeSelectedDate : (LocalDate) -> Unit,
    indexPagerMonth : Int
){
    //Determine the circle size to overlaps the number of days in calendar
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodyMedium
    val circleSize = remember {
        val textLayoutResult: TextLayoutResult =
            textMeasurer.measure(
                text = AnnotatedString("313"),
                style = textStyle
            )

        with(density) { textLayoutResult.size.width.toDp() }
    }

    //************************* PAGER STATE FUNCTIONS *******************
    val pagerState = rememberPagerState(
        initialPage = CalendarUtils.monthsSinceEpoch(LocalDate.now())
    ) {
        CalendarUtils.monthsInMaxIntDays()
    }

    //If calendar is swipe change the current date to the first day of the month
    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (pagerState.isScrollInProgress) onChangeSelectedDate(CalendarUtils.getLocalDateFromMonthsSinceEpoch(page))
        }
    }

    //Keep calendar update with the current selected date
    val coroutine = rememberCoroutineScope()
    LaunchedEffect(key1 = indexPagerMonth){
        if (pagerState.currentPage != indexPagerMonth ){
            coroutine.launch {
                pagerState.scrollToPage(indexPagerMonth)
            }
        }
    }


    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
    ) { page ->

        val days = remember { CalendarClient().getDaysOfMonth( CalendarUtils.getLocalDateFromMonthsSinceEpoch(page) ) }

        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            WeekdayHeader()

            days.chunked(7).forEach { week ->
                WeekRow(
                    week,
                    circleSize = circleSize,
                    selectedDate = selectedDate,
                    onDateSelected = {
                        onChangeSelectedDate(it)
                    }
                )
            }
        }
    }

}



@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        DayOfWeek.values().forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())[0].toString().uppercase() ,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f),
            )
        }
    }
}

@Composable
fun WeekRow(
    week: List<DateCalendar?>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    circleSize : Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (date in week) {
            if (date != null) {

                CalendarDay(
                    dateNumber = date.date.dayOfMonth.toString(),
                    isSelected = date.date.isEqual(selectedDate),
                    isToday = date.isToday,
                    onDateSelected = { onDateSelected(date.date) },
                    circleSize = circleSize,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun CalendarDay(
    dateNumber: String,
    isSelected: Boolean,
    isToday : Boolean,
    circleSize : Dp,
    onDateSelected: () -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .clickable(
                indication = null, //Remove ripple effect
                interactionSource = remember { MutableInteractionSource() } // This is mandatory
            ) { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .background(
                    color = when {
                        isToday -> MaterialTheme.colorScheme.tertiary
                        isSelected -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2F)
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                )
        )

        Text(
            text = dateNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
private fun MonthPreview() {
    MonpotagerTheme {
        MonthCalendarView(
            selectedDate = LocalDate.now(),
            onChangeSelectedDate = {},
            indexPagerMonth = CalendarUtils.monthsSinceEpoch(LocalDate.now())
        )
    }
}