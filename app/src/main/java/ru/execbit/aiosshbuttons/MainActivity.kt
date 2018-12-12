package ru.execbit.aiosshbuttons

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.WindowManager

@SuppressLint("ExportedPreferenceActivity")
class MainActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }
}
