package pro.progr.todos.composable

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import pro.progr.todos.R
import pro.progr.todos.work.SyncUpdateRequiredStore

/**
 * Rendered by TodosNavigation while the todos module is part of the app UI.
 */
@Composable
fun SyncUpdateRequiredDialog() {
    val context = LocalContext.current.applicationContext
    val preferences = remember(context) {
        context.getSharedPreferences(
            SyncUpdateRequiredStore.PREFERENCES_NAME,
            android.content.Context.MODE_PRIVATE
        )
    }
    var pending by remember(context) {
        mutableStateOf(SyncUpdateRequiredStore.getPending(context))
    }

    DisposableEffect(preferences, context) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            pending = SyncUpdateRequiredStore.getPending(context)
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        pending = SyncUpdateRequiredStore.getPending(context)
        onDispose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    if (pending != null) {
        AlertDialog(
            onDismissRequest = {
                SyncUpdateRequiredStore.dismiss(context)
                pending = null
            },
            title = { Text(stringResource(R.string.sync_update_required_title)) },
            text = { Text(stringResource(R.string.sync_update_required_message)) },
            confirmButton = {
                TextButton(onClick = {
                    SyncUpdateRequiredStore.dismiss(context)
                    pending = null
                }) {
                    Text(stringResource(R.string.sync_update_required_dismiss))
                }
            }
        )
    }
}
