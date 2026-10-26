package pro.progr.todos.work

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat

data class SyncUpdateRequired(
    val minimumSyncProtocolVersion: Int?
)

/** Persists the update warning across process restarts and dismissals. */
object SyncUpdateRequiredStore {
    internal const val PREFERENCES_NAME = "todos_sync_compatibility"
    private const val KEY_REQUIRED_MIN_VERSION = "required_minimum_protocol_version"
    private const val KEY_REQUIRED_APP_VERSION = "required_app_version_code"
    private const val KEY_DISMISSED_MIN_VERSION = "dismissed_minimum_protocol_version"
    private const val KEY_DISMISSED_APP_VERSION = "dismissed_app_version_code"
    private const val NULL_VERSION = -1

    fun record(context: Context, minimumSyncProtocolVersion: Int?): Boolean {
        val appContext = context.applicationContext
        val preferences = preferences(appContext)
        val appVersion = appVersionCode(appContext)
        val minimumVersion = minimumSyncProtocolVersion ?: NULL_VERSION

        if (preferences.getLong(KEY_DISMISSED_APP_VERSION, -1L) == appVersion
            && preferences.getInt(KEY_DISMISSED_MIN_VERSION, NULL_VERSION) == minimumVersion
        ) {
            Log.i(TAG, "Update warning already dismissed for this app/protocol version")
            return true
        }

        val saved = preferences.edit()
            .putInt(KEY_REQUIRED_MIN_VERSION, minimumVersion)
            .putLong(KEY_REQUIRED_APP_VERSION, appVersion)
            .commit()
        Log.i(TAG, "Update warning saved=$saved appVersion=$appVersion minimumProtocolVersion=$minimumVersion")
        return saved
    }

    fun getPending(context: Context): SyncUpdateRequired? {
        val appContext = context.applicationContext
        val preferences = preferences(appContext)
        if (!preferences.contains(KEY_REQUIRED_MIN_VERSION)) return null

        val savedAppVersion = preferences.getLong(KEY_REQUIRED_APP_VERSION, -1L)
        val currentAppVersion = appVersionCode(appContext)
        if (savedAppVersion != currentAppVersion) {
            clearAll(preferences)
            return null
        }

        val minimumVersion = preferences.getInt(KEY_REQUIRED_MIN_VERSION, NULL_VERSION)
        if (preferences.getLong(KEY_DISMISSED_APP_VERSION, -1L) == currentAppVersion
            && preferences.getInt(KEY_DISMISSED_MIN_VERSION, NULL_VERSION) == minimumVersion
        ) {
            return null
        }

        return SyncUpdateRequired(minimumVersion.takeUnless { it == NULL_VERSION })
    }

    fun dismiss(context: Context) {
        val appContext = context.applicationContext
        val preferences = preferences(appContext)
        val pending = getPending(appContext) ?: return
        val minimumVersion = pending.minimumSyncProtocolVersion ?: NULL_VERSION
        val appVersion = appVersionCode(appContext)

        preferences.edit()
            .remove(KEY_REQUIRED_MIN_VERSION)
            .remove(KEY_REQUIRED_APP_VERSION)
            .putInt(KEY_DISMISSED_MIN_VERSION, minimumVersion)
            .putLong(KEY_DISMISSED_APP_VERSION, appVersion)
            .commit()
    }

    private fun clearAll(preferences: SharedPreferences) {
        preferences.edit()
            .remove(KEY_REQUIRED_MIN_VERSION)
            .remove(KEY_REQUIRED_APP_VERSION)
            .remove(KEY_DISMISSED_MIN_VERSION)
            .remove(KEY_DISMISSED_APP_VERSION)
            .commit()
    }

    private fun preferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private fun appVersionCode(context: Context): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(packageInfo)
    }

    private const val TAG = "SyncUpdateRequiredStore"
}
