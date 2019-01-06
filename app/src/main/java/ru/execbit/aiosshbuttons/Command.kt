package ru.execbit.aiosshbuttons

import android.preference.Preference

data class Command(
    var name: String,
    var cmd: String,
    var color: Int = 0,
    var showResult: Boolean = false,
    var pref: Preference? = null
)