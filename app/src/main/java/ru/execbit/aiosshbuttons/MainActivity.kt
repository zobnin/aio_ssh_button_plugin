package ru.execbit.aiosshbuttons

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings as SystemSettings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        if (Build.VERSION.SDK_INT >= 23 &&
            Settings.showBatteryOptWarning &&
            !isIgnoringBatteryOptimizations()
        ) {
            showBatteryOptDialog()
            Settings.showBatteryOptWarning = false
        }
    }

    private fun showBatteryOptDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.warning)
            setMessage(R.string.chinese_pm_warning)
            setCancelable(false)

            setPositiveButton(R.string.allow) { _, _ ->
                try {
                    startActivity(
                        Intent(SystemSettings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = "package:$packageName".toUri()
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            setNegativeButton(R.string.cancel) { _, _ -> }

            create()
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }
}
