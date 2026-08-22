package ru.execbit.aiosshbuttons

import android.widget.LinearLayout
import kotlin.math.roundToInt

fun LinearLayout.setDefaultPaddingForDialog() {
    val density = resources.displayMetrics.density
    setPadding(
        (24 * density).roundToInt(),
        (16 * density).roundToInt(),
        (24 * density).roundToInt(),
        (16 * density).roundToInt()
    )
}
