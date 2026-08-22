package ru.execbit.aiosshbuttons

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var commandObjects: MutableList<Command>
    private lateinit var commandsCategory: PreferenceCategory

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        commandObjects = Settings.getCommandsObjects().toMutableList()
        commandsCategory = requireNotNull(findPreference("commands"))

        findPreference<Preference>("add_command")?.setOnPreferenceClickListener {
            showEditCommandDialog()
            true
        }

        setSettingsSummary()
        addCommandsToScreen()
    }

    private fun setSettingsSummary() {
        findPreference<Preference>("host")?.summary = Settings.host
        findPreference<Preference>("port")?.summary = Settings.port
        findPreference<Preference>("user")?.summary = Settings.user
        findPreference<Preference>("password")?.summary = "********"
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
        cmdObj.pref?.let(commandsCategory::removePreference)
    }

    private fun changeCommandOnScreen(cmdObj: Command) {
        cmdObj.pref?.apply {
            title = cmdObj.name
            summary = cmdObj.cmd
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        setSettingsSummary()
        Settings.isChanged = true
    }

    private fun showEditCommandDialog(cmdObj: Command? = null) {
        val context = requireContext()
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setDefaultPaddingForDialog()
        }
        val nameView = EditText(context).apply {
            setText(cmdObj?.name.orEmpty())
            setSelection(text.length)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val cmdView = EditText(context).apply {
            setText(cmdObj?.cmd.orEmpty())
            setSelection(text.length)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val showResultCheckBox = CheckBox(context).apply {
            setText(R.string.show_result)
            isChecked = cmdObj?.showResult ?: false
        }

        content.addView(TextView(context).apply { setText(R.string.name_) })
        content.addView(nameView)
        content.addView(TextView(context).apply { setText(R.string.command_) })
        content.addView(cmdView)
        content.addView(showResultCheckBox)

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.add_command)
            .setView(content)
            .setPositiveButton(R.string.save) { _, _ ->
                val nameStr = nameView.text.toString()
                val cmdStr = cmdView.text.toString()

                if (nameStr.isNotEmpty() && cmdStr.isNotEmpty()) {
                    if (cmdObj == null) {
                        val commandObj = Command(
                            nameStr,
                            cmdStr,
                            showResult = showResultCheckBox.isChecked
                        )
                        addCommandToScreen(commandObj)
                        commandObjects.add(commandObj)
                    } else {
                        cmdObj.name = nameStr
                        cmdObj.cmd = cmdStr
                        cmdObj.showResult = showResultCheckBox.isChecked
                        changeCommandOnScreen(cmdObj)
                    }

                    Settings.saveCommandObjects(commandObjects)
                    Settings.isChanged = true
                }
            }

        if (cmdObj == null) {
            dialog.setNegativeButton(R.string.cancel) { _, _ -> }
        } else {
            dialog.setNegativeButton(R.string.delete) { _, _ ->
                removeCommandFromScreen(cmdObj)
                commandObjects.remove(cmdObj)
                Settings.saveCommandObjects(commandObjects)
                Settings.isChanged = true
            }
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }
}
