package gleynuco.com.monpotager.features.calendar.presentation.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gleynuco.com.monpotager.R
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayDialogModel
import gleynuco.com.monpotager.features.calendar.domain.EventDisplayModel
import gleynuco.com.monpotager.presentation.EditVegetable
import gleynuco.com.monpotager.presentation.Settings
import gleynuco.com.monpotager.presentation.sharecompose.TimePickerDialog
import gleynuco.com.monpotager.presentation.sharecompose.TitleSectionDetailVegetable
import gleynuco.com.monpotager.presentation.theme.MonpotagerTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private val _eventTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val _eventDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetEventDialog(
    sheetState : SheetState,
    data : EventDisplayDialogModel,
    onModifyData : (EventDisplayDialogModel) -> Unit,
    onSaveEvent : (EventDisplayModel) -> Unit,
    onDeleteEvent : (EventDisplayModel) -> Unit,
    onDismiss : () -> Unit,
) {
    var isHourError by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = data){
        if (isHourError){
            if (data.event.end > data.event.start) isHourError = false
        }
    }

      ModalBottomSheet(
          dragHandle = null,
          containerColor = MaterialTheme.colorScheme.background,
          sheetState = sheetState,
          onDismissRequest = { onDismiss() },
      ) {

          Column (
              modifier = Modifier.fillMaxSize()
          ) {

              TopBarEventDialog(
                  onClickCloseButton = onDismiss,
                  onClickSave = {
                        //first check if end event is not equal or after start event
                        if (data.event.end <= data.event.start){
                            isHourError = true
                        }else{
                            onSaveEvent(data.event)
                        }
                  },
                  onClickEdit = {
                      onModifyData(
                          data.copy(isEditMode = true)
                      )
                  },
                  onClickDelete = { onDeleteEvent(data.event)},
                  isEditMode = data.isEditMode
              )


              if (data.isEditMode){

                  ContentCreateModifyEvent(
                      event = data.event,
                      onModifyEvent = {
                          onModifyData(
                              data.copy(event = it)
                          )
                      },
                      isHourError = isHourError
                  )

              }else{
                  ContentShowEvent(event = data.event)
              }
          }

      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarEventDialog(
    onClickCloseButton : () -> Unit,
    onClickSave : () -> Unit,
    onClickEdit : () -> Unit,
    onClickDelete : () -> Unit,
    isEditMode : Boolean,
){

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onClickCloseButton
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        },
        title = {  },
        actions = {
            if (isEditMode){
                TextButton(
                    onClick = onClickSave,
                ) {
                    Text(
                        text = stringResource(id = R.string.save),
                    )
                }
            }else{
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(id = R.string.toolbar_edit)
                    )
                }

                IconButton(
                    onClick = onClickDelete
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        null
                    )
                }
            }
        },
    )
}


@Composable
private fun ContentShowEvent(
    event : EventDisplayModel,
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ){

        SectionShowEvent(
            iconResource = R.drawable.baseline_event_24
        ){
            Column {
                Text(
                    text = event.name ?: stringResource(id = R.string.event_no_title),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "${event.start.format(_eventDateFormatter)}  ${event.start.format(_eventTimeFormatter)} - ${event.end.format(_eventTimeFormatter)}"
                )
            }
        }

        event.description?.let{ descr ->
            SectionShowEvent(
                iconResource = R.drawable.ic_description_black_24dp
            ){
                Text(
                    text = descr
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))
    }
}


@Composable
private fun SectionShowEvent(
    iconResource : Int,
    content : @Composable () -> Unit
) {
    
    Row (
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp, top = 24.dp)
    ) {

        Spacer(modifier = Modifier.size(16.dp))


        Icon(
            painter = painterResource(id = iconResource),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))


        content()
        
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SectionShowEventPreview (){
    MonpotagerTheme {
        SectionShowEvent(
            iconResource = R.drawable.ic_list_24dp
        ) {
            Text(text = "test \n test\ntest")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SectionShowEvent2Preview (){
    MonpotagerTheme {
        SectionShowEvent(
            iconResource = R.drawable.ic_list_24dp
        ) {
            Column {
                Text(
                    text = "Test",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Mercredi 1 2023"
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentCreateModifyEvent(
    event : EventDisplayModel,
    onModifyEvent : (EventDisplayModel) -> Unit,
    isHourError : Boolean,
){
    var openDialogDatePicker by remember { mutableStateOf(false) }
    var openDialogTimePickerIsStart by remember { mutableStateOf<Boolean?>(null) }

    val shape = RoundedCornerShape(8.dp)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ){

        OutlinedTextField(
            value = event.name ?: "",
            label = { Text(text = stringResource(id = R.string.edit_title)) },
            onValueChange = {
                onModifyEvent(event.copy(name = it))
            },
            maxLines = 1,
            singleLine = true,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )


        TitleSectionDetailVegetable(
            textResource = R.string.date,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = { openDialogDatePicker = true },
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = 16.dp)
        ) {


                Text(
                    text = event.start.format(_eventDateFormatter),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.baseline_event_24),
                    contentDescription =  null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )

        }

        TitleSectionDetailVegetable(
            textResource = R.string.hours,
            modifier = Modifier.fillMaxWidth()
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedButton(
                onClick = { openDialogTimePickerIsStart = true },
                shape = shape,
                modifier = Modifier
                    .weight(1f)
            ) {

                Text(
                    text = event.start.format(_eventTimeFormatter) ,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            Icon(painter = painterResource(id = R.drawable.baseline_arrow_right_24), contentDescription = null )

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedButton(
                onClick = { openDialogTimePickerIsStart = false },
                shape = shape,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = event.end.format(_eventTimeFormatter),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        AnimatedVisibility(visible = isHourError) {


            Text(
                text = stringResource(id = R.string.event_hours_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)

            )

        }

        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
        )

        OutlinedTextField(
            value = event.description ?: "",
            label = { Text(text = stringResource(id = R.string.edit_description)) },
            onValueChange = {
                onModifyEvent(event.copy(description = it))
            },
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )



        Spacer(modifier = Modifier.size(24.dp))
    }


    //DIALOGS
    if (openDialogDatePicker){

        val state = rememberDatePickerState(
            initialSelectedDateMillis = event.start.toInstant(OffsetTime.now().offset).toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = {openDialogDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { dateInMillis ->
                            val date = Instant.ofEpochMilli(dateInMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            onModifyEvent(
                                event.copy(
                                    start = LocalDateTime.of(date, event.start.toLocalTime()),
                                    end = LocalDateTime.of(date, event.end.toLocalTime())
                                )
                            )

                        }
                        openDialogDatePicker = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.validate))
                }
            }
        ) {
            DatePicker(
                state = state
            )
        }
    }

    //TIME PICKER
    openDialogTimePickerIsStart?.let {

        val timePickerState = rememberTimePickerState(
            initialHour = if (it) event.start.hour else event.end.hour,
            initialMinute = if (it) event.start.minute else event.end.minute
        )



        TimePickerDialog(
            onDismissRequest = { openDialogTimePickerIsStart = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        onModifyEvent(
                            if (it){
                                event.copy(
                                    start = LocalDateTime.of(event.start.toLocalDate(), LocalTime.of(timePickerState.hour, timePickerState.minute)),
                                )
                            }else{
                                event.copy(
                                    end = LocalDateTime.of(event.end.toLocalDate(), LocalTime.of(timePickerState.hour, timePickerState.minute)),
                                )
                            }
                        )

                        openDialogTimePickerIsStart = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.validate))
                }
            }
        ) {
            TimePicker(state = timePickerState)
           /* Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ){
                TimePicker(state = timePickerState)
            }*/

        }
    }





}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ContentCreateModifyEventPreview (){
    MonpotagerTheme {
        ContentCreateModifyEvent(
            event = EventDisplayModel(
                start = LocalDateTime.of(2023,1,2,0,1),
                end = LocalDateTime.of(2023,1,2,1,1),
                description = "test"
            ),
            onModifyEvent = {},
            isHourError = false
        )
    }
}