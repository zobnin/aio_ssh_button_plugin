package ru.execbit.aiosshbuttons

import com.mohamadamin.kpreferences.preference.Preference

object Settings {
    var isChanged by Preference(false, "settings_changed")

    var host by Preference("192.168.0.1", "host")
    var port by Preference("22", "port")
    var user by Preference("", "user")
    var password by Preference("", "password")
    var commands by Preference("", "commands")

    var lastPluginUpdateCheck by Preference(0L, "last_update_check")
    var notifyShowedForVersion by Preference(BuildConfig.VERSION_CODE, "notify_showed")
    var pluginUid by Preference("", "plugin_uid")
    var commandIds by Preference("", "cmd_ids")

    fun getCommandsObjects(): List<Command> {
        try {
            if (!Settings.commands.contains("‖")) return emptyList()

            return Settings.commands
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
        Settings.commands = ""
        commandObjects.forEach { cmdObj ->
            val showResult = if (cmdObj.showResult) 1 else 0
            Settings.commands += "${cmdObj.name}|${cmdObj.cmd}|${cmdObj.color}|$showResult‖"
        }
    }
}