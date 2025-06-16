package pro.progr.todos.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pro.progr.flow.vm.FloatingElementViewModel

@Composable
fun MyFloatingActionButton(
    floatingElementViewModel: FloatingElementViewModel,
    smallActionsViewModel: SmallActionsViewModel,
    navHostController: NavHostController
) {
    val rotationAngle =
        animateFloatAsState(if (floatingElementViewModel.showScrim.value) 45f else 0f)

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AnimatedVisibility(visible = floatingElementViewModel.showScrim.value) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                DisposableEffect(Unit) {
                    onDispose {
                        smallActionsViewModel.selectedDate = null
                    }
                }

                // Всплывающие кнопки
                for (smallAction in smallActionsViewModel.getActions()) {
                    FloatingActionTextButton(smallAction, navHostController)
                }
//                FloatingActionTextButton("Добавить задачу", Color(0xff01579B))
//                FloatingActionTextButton("Добавить задачу на сегодня", Color(0xff1B5E20))
//                FloatingActionTextButton("Добавить задачу на завтра", Color(0xff004D40))
            }
        }

        // Основная кнопка
        FloatingActionButton(
            onClick = {
                floatingElementViewModel.showScrim.value = !floatingElementViewModel.showScrim.value
            },
            content = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier.rotate(rotationAngle.value)
                )
            },
            backgroundColor = Color(0xff01579B),
            modifier = Modifier
                .border(
                    2.dp,
                    MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(percent = 100)
                )
        )
    }
}

@Composable
fun FloatingActionTextButton(
    smallActionModel: SmallActionModel,
    navHostController: NavHostController
) {
//    Surface(
//        modifier = Modifier
//            .clip(RoundedCornerShape(50))
//            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f)),
//        color = Color.Transparent
//    ) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navHostController.navigate(smallActionModel.destination)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = smallActionModel.text,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .shadow(elevation = 1.dp)
                .background(MaterialTheme.colors.surface)
                .padding(8.dp),
            style = MaterialTheme.typography.button
        )
        Spacer(Modifier.width(16.dp))
        CircleColor(backgroundColor = smallActionModel.color)
    }
//    }
}

@Composable
fun CircleColor(backgroundColor: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .shadow(elevation = 5.dp)
            .size(30.dp)
            .background(backgroundColor)
        //.padding(3.dp)
        //.border(width = 1.dp, color = MaterialTheme.colors.surface, shape = CircleShape)

    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(16.dp),
            tint = MaterialTheme.colors.surface
        )
    }
}