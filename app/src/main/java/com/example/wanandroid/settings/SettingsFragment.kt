package com.example.wanandroid.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.wanandroid.BuildConfig
import com.example.wanandroid.R
import com.example.wanandroid.main.MainActivity
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment.Companion.DISABLE_HISTORY
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment.Companion.KEY_IS_DISABLE
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsFragment : PreferenceFragmentCompat() {
    private var enableHistoryPreference: SwitchPreferenceCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val urlOpenInBrowser = findPreference<SwitchPreferenceCompat>(getString(
            R.string.settings_url_open_in_browser_key))
        urlOpenInBrowser?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                // 重启应用
                startActivity(Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                true
            }

        enableHistoryPreference = findPreference(getString(R.string.settings_enable_history_key))
        enableHistoryPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue == false) {
                    // 关闭浏览历史时弹一个对话框提示用户浏览历史会被清除
                    val alertDialog = HistoryDisableAlertDialogFragment().apply {
                        setTargetFragment(this@SettingsFragment, DISABLE_HISTORY)
                    }
                    alertDialog.show(requireActivity().supportFragmentManager,
                        HistoryDisableAlertDialogFragment.SHOW_TAG)
                }
                true
            }

        // 设置夜间模式
        val enableNightModePreference = findPreference<SwitchPreferenceCompat>(getString(
            R.string.settings_enable_night_mode_key))
        enableNightModePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    // 打开夜间模式
                } else {
                    // 关闭夜间模式
                }
                true
            }

        // 设置版本信息
        val versionInfoPreference = findPreference<Preference>(getString(R.string.settings_version_info_key))
        versionInfoPreference?.summaryProvider = Preference.SummaryProvider<Preference> {
            "${getString(R.string.current_version)} ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        }

        // 打开开源许可
        val openSourceLicensePreference = findPreference<Preference>(getString(
            R.string.settings_open_source_license_key))
        openSourceLicensePreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.settings_open_source_license_title))
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DISABLE_HISTORY && resultCode == Activity.RESULT_OK) {
            val isDisable = data?.getBooleanExtra(KEY_IS_DISABLE, false) ?: false
            enableHistoryPreference?.isChecked = !isDisable
        }
    }
}