package ru.execbit.aiolauncher.plugin

import android.content.Context
import android.content.Intent
import ru.execbit.aiolauncher.models.PluginIntentActions
import ru.execbit.aiolauncher.models.PluginResult
import ru.execbit.aiosshbuttons.Settings

fun Context.sendPluginResult(result: PluginResult) {
    val i = Intent(PluginIntentActions.AIO_UPDATE).apply {
        `package` = "ru.execbit.aiolauncher"
        putExtra("api", 1)
        putExtra("result", result)
        putExtra("uid", Settings.pluginUid)
    }

    sendBroadcast(i)
}

