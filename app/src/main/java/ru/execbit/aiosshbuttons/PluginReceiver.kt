package ru.execbit.aiosshbuttons

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.execbit.aiolauncher.models.*
import ru.execbit.aiolauncher.plugin.*
import java.io.ByteArrayOutputStream
import java.util.*

class PluginReceiver : BroadcastReceiver() {
    companion object {
        private var cn: ComponentName? = null
    }

    override fun onReceive(context: Context, intent: Intent?) {
        cn = ComponentName(context.packageName, context.packageName + ".PluginReceiver")

        CoroutineScope(Dispatchers.Default).launch {
            if (intent == null) return@launch
            if (!checkUid(intent)) return@launch

            when (intent.action) {
                PluginIntentActions.PLUGIN_GET_DATA -> processGetData(context, intent)
                PluginIntentActions.PLUGIN_SEND_ACTION -> processAction(context, intent)
            }

            Updater.checkForNewVersionAndShowNotify(context)
        }
    }

    private fun processAction(context: Context, intent: Intent) {
        intent.getParcelableExtra<PluginAction>("action")?.let { action ->
            try {
                when (action.context) {
                    "tap" -> processTapAction(context, action)
                    else -> {
                        /*
                        val result = PluginResult(
                            from = cn,
                            data = PluginError(4, "Invalid action")
                        )
                        context.sendPluginResult(result)
                        */
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processTapAction(context: Context, action: PluginAction) {
        try {
            getCommandById(action.selectedIds[0])?.let { cmd ->
                execCommand(context, cmd)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun execCommand(context: Context, cmd: Command) {
        val jsch = JSch()
        var session: Session? = null


        try {
            session = jsch.getSession(Settings.user, Settings.host, Settings.port.toInt())
            session.setPassword(Settings.password)

            // Avoid asking for key confirmation
            val prop = Properties()
            prop["StrictHostKeyChecking"] = "no"
            session.setConfig(prop)
            session.connect()

            // SSH Channel
            val channel = session.openChannel("exec") as ChannelExec
            val stream = ByteArrayOutputStream()
            channel.outputStream = stream

            // Execute command
            channel.setCommand(cmd.cmd)
            channel.connect(1000)
            Thread.sleep(100) // this kludge seemed to be required.
            channel.disconnect()

            if (cmd.showResult) {
                context.sendPluginResult(
                    PluginResult(
                        from = cn,
                        data = PluginDialog(
                            title = cmd.cmd,
                            text = stream.toString(),
                            bottomButtons = listOf(
                                PluginButton(
                                    text = context.getString(R.string.close),
                                    id = 0
                                )
                            )
                        )
                    )
                )
            } else {
                context.sendPluginResult(
                    PluginResult(
                        from = cn,
                        data = PluginMessage(context.getString(R.string.done))
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val result = PluginResult(
                from = cn,
                data = PluginError(2, e.toString())
            )
            context.sendPluginResult(result)
        } finally {
            session?.disconnect()
        }
    }

    private fun processGetData(context: Context, intent: Intent) {
        intent.getStringExtra("event")?.let { event ->
            when (event) {
                "load" -> generateAndSendResult(context)
                "force" -> generateAndSendResult(context)
                "resume" -> {
                    if (Settings.isChanged) {
                        Settings.isChanged = false
                        generateAndSendResult(context)
                    }
                }
            }
        }
    }

    private fun generateAndSendResult(context: Context) {
        context.sendPluginResult(generateResult(context))
    }

    private fun generateResult(context: Context): PluginResult {
        val commandObjects = Settings.getCommandsObjects()
        if (commandObjects.isEmpty()) {
            return PluginResult(
                from = cn,
                data = PluginButtons(emptyList())
            )
        }

        try {
            val ids = generateIds(commandObjects.size)
            val buttons = mutableListOf<PluginButton>()

            commandObjects.forEachIndexed { idx, cmd ->
                val button = PluginButton(
                    text = cmd.name,
                    clickable = true,
                    id = ids[idx]
                )
                buttons.add(button)
            }

            Settings.commandIds = idsToString(ids)

            return PluginResult(
                from = cn,
                data = PluginButtons(buttons)
            )

        } catch (e: Exception) {
            e.printStackTrace()

            return PluginResult(
                from = cn,
                data = PluginError(2, e.toString())
            )
        }
    }

    private fun getCommandById(id: Int): Command? {
        val ids = stringToIds(Settings.commandIds)

        if (ids.isEmpty()) {
            return null
        }

        val smsIdx = ids.indexOf(id)
        return Settings.getCommandsObjects()[smsIdx]
    }
}