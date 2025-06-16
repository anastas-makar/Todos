package pro.progr.todos.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.NoteCalendarViewModel
import pro.progr.flow.composable.GridCalendar

@Composable
fun BottomCalendar(calendarViewModel : NoteCalendarViewModel) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,/*colorResource(id = R.color.opaque_grey),*/
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
                .padding(top = 20.dp),
        ) {
            //todo:
            GridCalendar(calendarViewModel)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(2.dp)
                .align(Alignment.BottomCenter)
        )

    }
}