package ru.execbit.aiosshbuttons

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.text.InputType
import android.widget.CheckBox
import android.widget.EditText
import org.jetbrains.anko.*

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var commandObjects: MutableList<Command>
    private lateinit var commandsCategory: PreferenceCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        commandObjects = Settings.getCommandsObjects().toMutableList()
        commandsCategory = findPreference("commands") as PreferenceCategory

        findPreference("add_command")?.setOnPreferenceClickListener {
            showEditCommandDialog()
            true
        }

        setSettingsSummary()
        addCommandsToScreen()
    }

    private fun setSettingsSummary() {
        findPreference("host")?.summary = Settings.host
        findPreference("port")?.summary = Settings.port
        findPreference("user")?.summary = Settings.user
        findPreference("password")?.summary = "********"
    }

    private fun addCommandsToScreen() {
        commandObjects.forEach { cmdObj ->
            addCommandToScreen(cmdObj)
        }
    }

    private fun addCommandToScreen(cmdObj: Command) {
        val commandPref = Preference(preferenceScreen.context).apply {
            title = cmdObj.name
            summary = cmdObj.cmd

            setOnPreferenceClickListener {
                showEditCommandDialog(cmdObj)
                true
            }
        }

        commandsCategory.addPreference(commandPref)
        cmdObj.pref = commandPref
    }

    private fun removeCommandFromScreen(cmdObj: Command) {
        commandsCategory.removePreference(cmdObj.pref)
    }

    private fun changeCommandOnScreen(cmdObj: Command) {
        cmdObj.pref?.apply {
            title = cmdObj.name
            summary = cmdObj.cmd
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        setSettingsSummary()
        Settings.isChanged = true
    }

    private fun showEditCommandDialog(cmdObj: Command? = null) {
        var nameView: EditText? = null
        var cmdView: EditText? = null
        var showResultCheckBox: CheckBox? = null

        alert {
            title = getString(R.string.add_command)
            customView {
                verticalLayout {
                    setDefaultPaddingForDialog()

                    textView { setText(R.string.name_) }
                    nameView = editText {
                        setText(cmdObj?.name ?: "")
                        inputType = InputType.TYPE_TEXT_VARIATION_FILTER
                    }

                    nameView?.post {
                        nameView?.setSelection(nameView?.text?.length ?: 0)
                    }

                    textView { setText(R.string.command_) }
                    cmdView = editText {
                        setText(cmdObj?.cmd ?: "")
                        setSelection(this.text.length)
                        inputType = InputType.TYPE_TEXT_VARIATION_FILTER
                    }

                    cmdView?.post {
                        cmdView?.setSelection(cmdView?.text?.length ?: 0)
                    }

                    showResultCheckBox = checkBox {
                        setText(R.string.show_result)
                        isChecked = cmdObj?.showResult ?: false
                    }
                }
            }
            positiveButton(R.string.save) {
                val nameStr = nameView?.text.toString()
                val cmdStr = cmdView?.text.toString()

                if (nameStr.isNotEmpty() && nameStr.isNotEmpty()) {
                    if (cmdObj == null) {
                        val commandObj = Command(nameStr, cmdStr,
                            showResult = showResultCheckBox?.isChecked ?: false)

                        addCommandToScreen(commandObj)
                        commandObjects.add(commandObj)
                    } else {
                        cmdObj.name = nameStr
                        cmdObj.cmd = cmdStr
                        cmdObj.showResult = showResultCheckBox?.isChecked ?: false
                        changeCommandOnScreen(cmdObj)
                    }

                    Settings.saveCommandObjects(commandObjects)
                    Settings.isChanged = true
                }
            }
            if (cmdObj == null) {
                negativeButton(R.string.cancel) {
                    // nop
                }
            } else {
                negativeButton(R.string.delete) {
                    removeCommandFromScreen(cmdObj)
                    commandObjects.remove(cmdObj)
                    Settings.saveCommandObjects(commandObjects)
                    Settings.isChanged = true
                }
            }
        }.show()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}
