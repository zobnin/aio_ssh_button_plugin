package ru.execbit.aiosshbuttons

import android.widget.LinearLayout
import org.jetbrains.anko.*

fun LinearLayout.setDefaultPaddingForDialog() {
    leftPadding = dip(24)
    rightPadding = dip(24)
    topPadding = dip(16)
    bottomPadding = dip(16)
}

