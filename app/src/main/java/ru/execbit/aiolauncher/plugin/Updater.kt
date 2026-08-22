package ru.execbit.aiolauncher.plugin

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.execbit.aiosshbuttons.App
import ru.execbit.aiosshbuttons.BuildConfig
import ru.execbit.aiosshbuttons.R
import ru.execbit.aiosshbuttons.Settings
import java.util.*

object Updater {
    data class PluginMeta(
        val name: String,
        val version: String,
        val versionCode: Int
    )

    private const val REPO_HOST = "https://execbit.ru/aiolauncher/"
    private val packageUrl = REPO_HOST + BuildConfig.APPLICATION_ID + ".apk"
    private val metaUrl = REPO_HOST + BuildConfig.APPLICATION_ID + ".meta"

    fun checkForNewVersionAndShowNotify(context: Context) {
        val time = Date().time

        if (Settings.lastPluginUpdateCheck == 0L) {
            Settings.lastPluginUpdateCheck = time
            return
        }

        // Check once a day
        if (Settings.lastPluginUpdateCheck + 86400000 < time) {
            val meta = getPluginMeta()

            if (meta == null) {
                // Try again in an hour
                Settings.lastPluginUpdateCheck += 3600000
            } else {
                Settings.lastPluginUpdateCheck = time

                if (Settings.notifyShowedForVersion < meta.versionCode) {
                    showNotification(context, meta)
                    Settings.notifyShowedForVersion = meta.versionCode
                }
            }
        }
    }

    private fun getPluginMeta(): PluginMeta? {
        try {
            val request = Request.Builder()
                .url(metaUrl)
                .build()

            val okHttpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    return parseJson(response.body.string())
                }
            }

            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun showNotification(context: Context, meta: PluginMeta) {
        val browserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = packageUrl.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent
                = PendingIntent.getActivity(context, 0, browserIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.new_version_available, meta.version))
            .setContentText(context.getString(R.string.click_to_download))
            .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0, notification)
    }

    private fun parseJson(jsonStr: String): PluginMeta? {
        return try {
            val jsonObj = JSONObject(jsonStr)

            PluginMeta(
                name = jsonObj.getString("name"),
                version = jsonObj.getString("version"),
                versionCode = jsonObj.getInt("versionCode")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
