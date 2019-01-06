package ru.execbit.aiolauncher.models

import android.content.ComponentName
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object PluginIntentActions {
    const val PLUGIN_SETTINGS = "ru.execbit.aiolauncher.PLUGIN_SETTINGS"
    const val PLUGIN_GET_DATA = "ru.execbit.aiolauncher.PLUGIN_GET_DATA"
    const val PLUGIN_SEND_ACTION = "ru.execbit.aiolauncher.PLUGIN_SEND_ACTION"
    const val AIO_UPDATE = "ru.execbit.aiolauncher.AIO_UPDATE"
}

@Parcelize
class PluginResult(
    // ComponentName of the plugin
    val from: ComponentName? = null,
    
    // One of:
    // * PluginLines
    // * PluginButtons
    // * PluginDialog
    // * PluginMenu
    // * PluginMessage
    // * PluginError
    val data: Parcelable? = null
): Parcelable

// Data types

@Parcelize
data class PluginLines(
    val lines: List<PluginLine>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false,
    // Not used
    val showDots: Boolean = false
): Parcelable

@Parcelize
data class PluginButtons(
    val buttons: List<PluginButton>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false
): Parcelable

@Parcelize
data class PluginMenu(
    val buttons: List<PluginButton>
): Parcelable

@Parcelize
data class PluginLine(
        val body: String = "",
        val from: String = "",
        // Not implemented yet
        val textColor: Int = 0,
        // Not implemented yet
        val backgroundColor: Int = 0,
        // Not implemented yet
        val dotColor: Int = 0,
        // Not implemented yet
        val extra: String = "",
        val clickable: Boolean = false,
        val id: Int
): Parcelable

@Parcelize
data class PluginButton(
        val text: String = "",
        // Used only by menu buttons
        val icon: Bitmap? = null,
        // Not implemented yet
        val textColor: Int = 0,
        val backgroundColor: Int = 0,
        // Not implemented yet, used only by regular buttons
        val badge: Int = 0,
        // Not implemented yet
        val extra: String = "",
        val clickAnimation: Boolean = false,
        val id: Int
): Parcelable

@Parcelize
data class PluginDialog(
        val title: String,
        val text: String = "",
        val radioButtons: List<PluginRadioButton>? = null,
        val checkBoxes: List<PluginCheckBox>? = null,
        val bottomButtons: List<PluginButton>? = null
): Parcelable

@Parcelize
data class PluginRadioButton(
        val text: String,
        var checked: Boolean = false,
        val id: Int
): Parcelable

@Parcelize
data class PluginCheckBox(
        val text: String,
        var checked: Boolean = false,
        val id: Int
): Parcelable

@Parcelize
data class PluginError(
    // 0 - ok
    // 1 - no permission
    // 2 - exception
    // >= 100 - other
    val errorCode: Int,
    val errorText: String
): Parcelable

@Parcelize
data class PluginMessage(
    val text: String
): Parcelable

// From launcher to plugin

@Parcelize
data class PluginAction(
    // tap - user clicked item
    // longtap - user long clicked item
    // dialog - user clicked dialog element
    // menu - user clicked menu element
    val context: String = "tap",
    val selectedIds: List<Int>
): Parcelable


