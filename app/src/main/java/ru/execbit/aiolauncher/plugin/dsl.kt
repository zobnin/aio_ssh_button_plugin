package ru.execbit.aiolauncher.plugin

import android.content.ComponentName
import android.graphics.Bitmap
import android.os.Parcelable
import ru.execbit.aiolauncher.models.*

class PluginResultBuilder {
    var from: ComponentName? = null
    var data: Parcelable? = null

    fun buttons(lambda: ResultButtonsBuilder.() -> Unit) {
        data = ResultButtonsBuilder().apply(lambda).build()
    }

    fun dialog(lambda: ResultDialogBuilder.() -> Unit) {
        data = ResultDialogBuilder().apply(lambda).build()
    }

    fun message(lambda: ResultMessageBuilder.() -> Unit) {
        data = ResultMessageBuilder().apply(lambda).build()
    }

    fun error(lambda: ResultErrorBuilder.() -> Unit) {
        data = ResultErrorBuilder().apply(lambda).build()
    }

    fun build() = PluginResult(from, data)
}

class ResultDialogBuilder {
    var title = ""
    var text = ""
    private var buttonsList = mutableListOf<PluginButton>()

    fun buttons(lambda: ButtonsBuilder.() -> Unit) {
        buttonsList.addAll(ButtonsBuilder().apply(lambda).build())
    }

    fun build() = PluginDialog(title, text, bottomButtons = buttonsList)
}

class ResultMessageBuilder {
    var text = ""

    fun build() = PluginMessage(text)
}

class ResultErrorBuilder {
    var errorCode = 0
    var errorText = ""

    fun build() = PluginError(errorCode, errorText)
}

class ResultButtonsBuilder {
    var buttons = mutableListOf<PluginButton>()
    var maxLines = 3
    var foldable = true
    var privateModeSupport = false

    fun button(lambda: PluginButtonBuilder.() -> Unit) {
        buttons.add(PluginButtonBuilder().apply(lambda).build())
    }

    fun build() = PluginButtons(
        buttons = buttons,
        maxLines = maxLines,
        foldable = foldable,
        privateModeSupport = privateModeSupport
    )
}

class ButtonsBuilder {
    private val buttonsList = mutableListOf<PluginButton>()

    fun button(lambda: PluginButtonBuilder.() -> Unit) {
        buttonsList.add(PluginButtonBuilder().apply(lambda).build())
    }

    fun build() = buttonsList
}

class PluginButtonBuilder {
    var text: String = ""
    var icon: Bitmap? = null
    var textColor: Int = 0
    var backgroundColor: Int = 0
    var badge: Int = 0
    var extra: String = ""
    var clickAnimation: Boolean = false
    var id: Int = 0

    fun build() = PluginButton(
        text = text,
        icon = icon,
        textColor = textColor,
        backgroundColor = backgroundColor,
        badge = badge,
        extra = extra,
        clickAnimation = clickAnimation,
        id = id
    )
}

fun pluginResult(lambda: PluginResultBuilder.() -> Unit): PluginResult {
    return PluginResultBuilder().apply(lambda).build()
}
