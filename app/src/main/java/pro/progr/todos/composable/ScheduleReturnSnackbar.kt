package pro.progr.todos.composable

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pro.progr.todos.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleReturnSnackbar(
    route: String,
    navController: NavHostController,
    scaffoldState: ScaffoldState
) {
    val context = LocalContext.current
    val currentEntry = navController.currentBackStackEntryAsState().value

    LaunchedEffect(currentEntry, route, scaffoldState) {
        val entry = currentEntry ?: return@LaunchedEffect
        if (entry.destination.route != route) return@LaunchedEffect

        val notice = entry.savedStateHandle.remove<String>(NOTICE_KEY)
            ?: return@LaunchedEffect

        val (messageId, actionId, destination) = when (notice) {
            NOT_IN_CALENDAR -> Triple(
                R.string.task_not_in_calendar_notice,
                R.string.open_not_in_calendar_list,
                "list"
            )
            IN_CALENDAR -> Triple(
                R.string.task_added_to_calendar_notice,
                R.string.open_calendar,
                "calendar"
            )
            else -> return@LaunchedEffect
        }

        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = context.getString(messageId),
            actionLabel = context.getString(actionId)
        )
        if (result == SnackbarResult.ActionPerformed) {
            navController.navigate(destination)
        }
    }
}

const val NOTICE_KEY = "todosScheduleReturnNotice"
const val NOT_IN_CALENDAR = "not_in_calendar"
const val IN_CALENDAR = "in_calendar"
