package ru.execbit.aiosshbuttons

import android.app.Application
import com.mohamadamin.kpreferences.base.KPreferenceManager
import com.singhajit.sherlock.core.Sherlock

class App: Application() {
    companion object {
        var PACKAGE_NAME: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        PACKAGE_NAME = packageName
        Sherlock.init(this)
        KPreferenceManager.initialize(this, name = "${packageName}_preferences")
    }
}