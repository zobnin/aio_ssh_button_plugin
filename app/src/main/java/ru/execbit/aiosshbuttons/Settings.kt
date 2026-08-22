package ru.execbit.aiosshbuttons

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Settings {
    private val preferences: SharedPreferences
        get() = App.instance.getSharedPreferences(
            "${App.instance.packageName}_preferences",
            Context.MODE_PRIVATE
        )

    private class StoredPreference<T>(
        private val defaultValue: T,
        private val key: String
    ) : ReadWriteProperty<Any?, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return when (defaultValue) {
                is Boolean -> preferences.getBoolean(key, defaultValue)
                is Float -> preferences.getFloat(key, defaultValue)
                is Int -> preferences.getInt(key, defaultValue)
                is Long -> preferences.getLong(key, defaultValue)
                is String -> preferences.getString(key, defaultValue) ?: defaultValue
                else -> error("Unsupported preference type for $key")
            } as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            preferences.edit {
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is String -> putString(key, value)
                    else -> error("Unsupported preference type for $key")
                }
            }
        }
    }

    var isChanged by StoredPreference(false, "settings_changed")

    var host by StoredPreference("192.168.0.1", "host")
    var port by StoredPreference("22", "port")
    var user by StoredPreference("", "user")
    var password by StoredPreference("", "password")
    var commands by StoredPreference("", "commands")

    var lastPluginUpdateCheck by StoredPreference(0L, "last_update_check")
    var notifyShowedForVersion by StoredPreference(BuildConfig.VERSION_CODE, "notify_showed")
    var pluginUid by StoredPreference("", "plugin_uid")
    var commandIds by StoredPreference("", "cmd_ids")

    var showBatteryOptWarning by StoredPreference(true, "show_direct_battery_opt_warning")

    fun getCommandsObjects(): List<Command> {
        try {
            if (!commands.contains("‖")) return emptyList()

            return commands
                .dropLast(1)
                .split("‖")
                .map {
                    val (name, cmd, color, showResult)
                            = it.split("|")
                    Command(name, cmd, color.toInt(), showResult.toInt() == 1)
                }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun saveCommandObjects(commandObjects: List<Command>) {
        commands = ""
        commandObjects.forEach { cmdObj ->
            val showResult = if (cmdObj.showResult) 1 else 0
            commands += "${cmdObj.name}|${cmdObj.cmd}|${cmdObj.color}|$showResult‖"
        }
    }
}
