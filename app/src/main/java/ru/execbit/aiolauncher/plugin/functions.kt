package ru.execbit.aiolauncher.plugin

import android.content.Intent
import ru.execbit.aiosshbuttons.Settings
import java.util.*
import kotlin.collections.HashSet

fun checkUid(intent: Intent): Boolean {
    val uid = intent.getStringExtra("uid") ?: ""

    if (uid.isEmpty()) {
        return false
    }

    if (Settings.pluginUid.isEmpty()) {
        Settings.pluginUid = uid
        return true
    } else {
        if (Settings.pluginUid == uid) {
            return true
        }
    }

    return false
}

fun generateIds(num: Int): List<Int> {
    val r = Random()
    val uniqueNumbers = HashSet<Int>()

    while (uniqueNumbers.size < num) {
        // Generate only positive numbers
        uniqueNumbers.add(r.nextInt(Integer.MAX_VALUE))
    }

    return uniqueNumbers.toList()
}

fun idsToString(ids: List<Int>): String {
    var string = ""

    ids.forEach {
        string += "$it:"
    }

    return string.dropLast(1)
}

fun stringToIds(string: String): List<Int> {
    return string.split(':').map { it.toInt() }
}