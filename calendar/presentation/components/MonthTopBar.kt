package gleynuco.com.monpotager.features.calendar.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun MonthTopBar (
    title: String,
    onClickExpand : () -> Unit,
    isExpandAvailable : Boolean,
    isCalendarExpand : Boolean
){
    val rotationState by animateFloatAsState(if (!isCalendarExpand) -180f else 0f, label = "") // Rotation State
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults
            .centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            ),
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable (
                        enabled = isExpandAvailable
                    ) { onClickExpand() }
            ){
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Drop Down Arrow",
                    tint = Color.Transparent,
                )

                Text(text =  title ,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge)


                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Drop Down Arrow",
                    tint = if(isExpandAvailable) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                    modifier = Modifier
                        .rotate(rotationState),
                )

            }

        },
    )

}