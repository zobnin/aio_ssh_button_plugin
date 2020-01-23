package ru.execbit.aiosshbuttons

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.mohamadamin.kpreferences.base.KPreferenceManager
import com.singhajit.sherlock.core.Sherlock

class App: Application() {
    companion object {
        var PACKAGE_NAME: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        PACKAGE_NAME = packageName

        createNotificationChannel()
        Sherlock.init(this)
        KPreferenceManager.initialize(this, name = "${packageName}_preferences")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= 26) {
            val name = "AIO SSH Buttons Plugin"
            val descriptionText = "AIO SSH Buttons Plugin"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("main", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager? =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }
}