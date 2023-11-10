package gleynuco.com.monpotager.features.calendar.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import gleynuco.com.monpotager.features.calendar.data.CalendarUtils
import gleynuco.com.monpotager.features.calendar.data.model.EventModel
import gleynuco.com.monpotager.features.calendar.presentation.components.DayCalendarView
import gleynuco.com.monpotager.features.calendar.presentation.components.MonthCalendarView
import gleynuco.com.monpotager.features.calendar.presentation.components.MonthTopBar
import gleynuco.com.monpotager.features.calendar.presentation.components.dialogs.BottomSheetEventDialog
import gleynuco.com.monpotager.presentation.Moon
import gleynuco.com.monpotager.presentation.MyVegetableDetail
import gleynuco.com.monpotager.presentation.sharecompose.BottomSheetAddParcel
import gleynuco.com.monpotager.presentation.theme.MonpotagerTheme
import gleynuco.com.monpotager.utils.PreviewGetWindowSize.getWindowSize
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    state : CalendarState,
    onScreenEvent : (CalendarScreenEvent) -> Unit,
    navigateTo : (String) -> Unit,
    windowSize: WindowSizeClass
) {

    val verticalDayScrollState = rememberScrollState()


    LaunchedEffect(verticalDayScrollState) {
        snapshotFlow { verticalDayScrollState.isScrollInProgress }
            .collect {
                if (state.isCalendarExpanded){
                    if (verticalDayScrollState.value != 0) onScreenEvent(CalendarScreenEvent.OnChangeExpandedCalendar(false))

                }else{
                    if(verticalDayScrollState.value == 0) onScreenEvent(CalendarScreenEvent.OnChangeExpandedCalendar(true))
                }
            }
    }


    val pagerStateDay = rememberPagerState(
        initialPage = CalendarUtils.daysSinceEpoch(LocalDate.now())
    ) {
        Int.MAX_VALUE
    }

    var isCanScrollDayPager by remember {
        mutableStateOf(true)
    }
    //If calendar is swipe change the current date to the first day of the month
    LaunchedEffect(pagerStateDay) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerStateDay.currentPage }.collect { page ->

            println("coroutine -> currentpage $page")
            if (isCanScrollDayPager){
                onScreenEvent(CalendarScreenEvent.OnChangeCurrentDate(
                    newDate = CalendarUtils.localDateFromDaysSinceEpoch(page)
                ))
            }

        }
    }



    val coroutine = rememberCoroutineScope()
    LaunchedEffect(key1 = state.indexPagerDay){
        if (state.indexPagerDay != pagerStateDay.currentPage){
            coroutine.launch {
                isCanScrollDayPager = false
                coroutine.async {
                    pagerStateDay.animateScrollToPage(state.indexPagerDay)
                }.await()
                isCanScrollDayPager = true
            }
        }
    }



    when(windowSize.widthSizeClass){
        WindowWidthSizeClass.Compact -> {
            if (windowSize.heightSizeClass == WindowHeightSizeClass.Compact){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MonthTopBar(
                        title = state.title,
                        onClickExpand = {},
                        isExpandAvailable = false,
                        isCalendarExpand = false
                    )

                    DayCalendarView(
                        mapEvent = state.mapEvents,
                        onClickEvent = { onScreenEvent(CalendarScreenEvent.OnOpenEventDialog(it)) },
                        isLoadingEvent = state.isLoadingEvents,
                        verticalScrollState = verticalDayScrollState,
                        pagerStateDay = pagerStateDay,
                        isDayTopBarElevated = true,
                        enableSwipeGesture = isCanScrollDayPager,
                        moonPhase = state.moonPhase,
                        onClickNavigateMoon = {
                            navigateTo(
                                Moon.route.replace(
                                    oldValue = Moon.time,
                                    newValue = "$it"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }else{

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {


                    MonthTopBar(
                        title = state.title,
                        onClickExpand = {
                            onScreenEvent(CalendarScreenEvent.OnChangeExpandedCalendar(!state.isCalendarExpanded))
                        },
                        isExpandAvailable = true,
                        isCalendarExpand = state.isCalendarExpanded
                    )

                    AnimatedVisibility(state.isCalendarExpanded) {
                        MonthCalendarView(
                            selectedDate = state.currentSelectedDate,
                            onChangeSelectedDate = {
                                onScreenEvent(CalendarScreenEvent.OnChangeCurrentDate(it))
                              //  currentSelectedDate = it
                            },
                            indexPagerMonth = state.indexPagerMonth
                        )
                    }


                    DayCalendarView(
                        mapEvent = state.mapEvents,
                        onClickEvent = { onScreenEvent(CalendarScreenEvent.OnOpenEventDialog(it)) },
                        isLoadingEvent = state.isLoadingEvents,
                        verticalScrollState = verticalDayScrollState,
                        pagerStateDay = pagerStateDay,
                        isDayTopBarElevated = !state.isCalendarExpanded,
                        enableSwipeGesture = isCanScrollDayPager,
                        moonPhase = state.moonPhase,
                        onClickNavigateMoon = {
                            navigateTo(
                                Moon.route.replace(
                                    oldValue = Moon.time,
                                    newValue = "$it"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        else ->{
            if (windowSize.heightSizeClass == WindowHeightSizeClass.Compact){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MonthTopBar(
                        title = state.title,
                        onClickExpand = {},
                        isExpandAvailable = false,
                        isCalendarExpand = false
                    )

                    DayCalendarView(
                        mapEvent = state.mapEvents,
                        onClickEvent = { onScreenEvent(CalendarScreenEvent.OnOpenEventDialog(it)) },
                        isLoadingEvent = state.isLoadingEvents,
                        verticalScrollState = verticalDayScrollState,
                        pagerStateDay = pagerStateDay,
                        isDayTopBarElevated = true,
                        enableSwipeGesture = isCanScrollDayPager,
                        moonPhase = state.moonPhase,
                        onClickNavigateMoon = {
                            navigateTo(
                                Moon.route.replace(
                                    oldValue = Moon.time,
                                    newValue = "$it"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    MonthTopBar(
                        title = state.title,
                        onClickExpand = {
                            onScreenEvent(CalendarScreenEvent.OnChangeExpandedCalendar(!state.isCalendarExpanded))
                        },
                        isExpandAvailable = true,
                        isCalendarExpand = state.isCalendarExpanded
                    )

                    AnimatedVisibility(state.isCalendarExpanded) {
                        MonthCalendarView(
                            selectedDate = state.currentSelectedDate,
                            onChangeSelectedDate = {
                                onScreenEvent(CalendarScreenEvent.OnChangeCurrentDate(it))
                            },
                            indexPagerMonth = state.indexPagerMonth
                        )
                    }

                    DayCalendarView(
                        mapEvent = state.mapEvents,
                        onClickEvent = { onScreenEvent(CalendarScreenEvent.OnOpenEventDialog(it)) },
                        isLoadingEvent = state.isLoadingEvents,
                        verticalScrollState = verticalDayScrollState,
                        pagerStateDay = pagerStateDay,
                        enableSwipeGesture = isCanScrollDayPager,
                        isDayTopBarElevated = state.isCalendarExpanded,
                        moonPhase = state.moonPhase,
                        onClickNavigateMoon = {
                            navigateTo(
                                Moon.route.replace(
                                    oldValue = Moon.time,
                                    newValue = "$it"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }


    //DIALOGS
    state.displayEventDialog?.let { dataDialog ->
        val sheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Expanded,
            confirmValueChange = { !dataDialog.isEditMode }
        )

        BottomSheetEventDialog(
            sheetState = sheetState,
            data = dataDialog,
            onSaveEvent = { data -> onScreenEvent(CalendarScreenEvent.OnSaveEventDialog(data)) },
            onModifyData = { data -> onScreenEvent(CalendarScreenEvent.OnModifyEventDialogData(data)) } ,
            onDeleteEvent = { data -> onScreenEvent(CalendarScreenEvent.OnDeleteEventDialog(data)) },
            onDismiss = { onScreenEvent(CalendarScreenEvent.OnDismissEventDialog) }
        )
    }

}



@Preview(device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    device = "spec:width=411dp,height=891dp", showBackground = true,
    backgroundColor = 0xFFFFFFFF, name = "Phone Vertical"
)
@Preview(
    device = "spec:width=673dp,height=841dp", showBackground = true,
    backgroundColor = 0xFFFFFFFF, name = "foldable  Vertical"
)
@Composable
fun CalendarPreview() {
    MonpotagerTheme {
        val dm = LocalContext.current.resources.displayMetrics

        CalendarScreen(
            state = CalendarState(),
            onScreenEvent = {},
            windowSize = dm.getWindowSize(),
            navigateTo = {}
        )
    }
}