package gleynuco.com.monpotager.features.calendar.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gleynuco.com.monpotager.presentation.moonscreen.getRessourcePhaseName
import gleynuco.com.monpotager.presentation.theme.MonpotagerTheme
import gleynuco.com.monpotager.R
import gleynuco.com.monpotager.utils.Constants


@Composable
fun DayTopBar (
    dayName: String,
    dayNumber : String,
    isLoadingEvent : Boolean,
    moonPhase : Int,
    isElevated : Boolean,
    onClickNavigateMoon : () -> Unit
){

    val animBackgroundColor by animateColorAsState(if (!isElevated) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        label = ""
    )

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(animBackgroundColor)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ){
            SmallDayLabel(
                dayName = dayName,
                dayNumber = dayNumber,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            ButtonMoonCalendar(
                moonPhase = moonPhase,
                onClickButton = onClickNavigateMoon,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }


        AnimatedVisibility(visible = isLoadingEvent) {
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
        )
    }
}

@Composable
private fun ButtonMoonCalendar(
    moonPhase : Int,
    onClickButton : () -> Unit,
    modifier: Modifier = Modifier,
) {

    TextButton(
        onClick = onClickButton,
        modifier = modifier
    ) {

        Text(
            text = stringResource(id = getRessourcePhaseName(moonPhase)),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.size(8.dp))

        Image(
            painter = painterResource(id = R.drawable.moon_full),
            contentDescription = "",
            modifier = Modifier
                .size(30.dp)
        )
        
    }

}


@Composable
private fun SmallDayLabel(
    dayName: String,
    dayNumber : String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = dayNumber,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun TopBarDayPreview() {
    MonpotagerTheme {
        DayTopBar(
            dayName = "ven",
            dayNumber = "15",
            moonPhase = Constants.MOON_FULL_MOON,
            isElevated = false,
            isLoadingEvent = true,
            onClickNavigateMoon = {}
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun SmallDayLabelPreview() {
    MonpotagerTheme {
        SmallDayLabel(
            dayName = "ven",
            dayNumber = "15"
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun ButtonMoonCalendarPreview() {
    MonpotagerTheme {
        ButtonMoonCalendar(
            moonPhase = Constants.MOON_FULL_MOON,
            onClickButton = {},
            modifier = Modifier
        )
    }
}