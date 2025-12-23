package acr.browser.lightning.settings.fragment

import acr.browser.lightning.R
import acr.browser.lightning.browser.di.injector
import acr.browser.lightning.device.BuildInfo
import acr.browser.lightning.device.BuildType
import acr.browser.lightning.display.ScrollPrefs
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import javax.inject.Inject

/**
 * The root settings list.
 */
class RootSettingsFragment : AbstractSettingsFragment() {

    @Inject lateinit var buildInfo: BuildInfo

    override fun providePreferencesXmlResource(): Int = R.xml.preference_root

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        injector.inject(this)

        // DEBUG 项目是否显示
        preferenceManager.findPreference<Preference>(DEBUG_KEY)?.isVisible =
            buildInfo.buildType != BuildType.RELEASE

        // ⭐ 翻页步长（stepPercent）监听器
        val stepPref = findPreference<SeekBarPreference>("scroll_step_percent")
        stepPref?.setOnPreferenceChangeListener { _, newValue ->
            val percent = (newValue as Int) / 100f
            val newCfg = ScrollPrefs.current().copy(stepPercent = percent)
            ScrollPrefs.update(newCfg, requireContext())
            true
        }
    }

    companion object {
        private const val DEBUG_KEY = "DEBUG"
    }
}
