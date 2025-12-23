package acr.browser.lightning.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import acr.browser.lightning.R
import acr.browser.lightning.ui.float.FloatPagerPrefs

class FloatPagerSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.float_pager_prefs, rootKey)

        // ⭐ 实时预览：透明度
        findPreference<SeekBarPreference>("floatpager_opacity")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val v = (newValue as Int) / 100f
                activity?.window?.decorView?.alpha = v
                true
            }
        }

        // ⭐ 实时预览：临时透明度
        findPreference<SeekBarPreference>("floatpager_hide_opacity")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val v = (newValue as Int) / 100f
                activity?.window?.decorView?.alpha = v
                true
            }
        }

        // ⭐ 实时预览：按钮尺寸（dp）
        findPreference<SeekBarPreference>("floatpager_main_size")?.apply {
            summary = "当前：${FloatPagerPrefs.loadMainButtonSize(requireContext())} dp"
            setOnPreferenceChangeListener { pref, newValue ->
                val size = newValue as Int
                pref.summary = "当前：$size dp"
                true
            }
        }

        // ⭐ 重置位置
        findPreference<Preference>("floatpager_reset_position")?.setOnPreferenceClickListener {
            FloatPagerPrefs.clearSavedPosition(requireContext())
            it.summary = "已重置（刷新页面后生效）"
            true
        }

        // ⭐ 恢复默认设置
        findPreference<Preference>("floatpager_restore_defaults")?.setOnPreferenceClickListener {
            restoreDefaults()
            true
        }
    }

    private fun restoreDefaults() {
        val ctx = requireContext()

        FloatPagerPrefs.setEnabled(ctx, true)
        FloatPagerPrefs.saveOpacity(ctx, 0.4f)
        FloatPagerPrefs.saveHideOpacity(ctx, 0.1f)
        FloatPagerPrefs.saveMainButtonSize(ctx, 48)
        FloatPagerPrefs.saveDefaultX(ctx, -1)
        FloatPagerPrefs.saveDefaultY(ctx, -1)
        FloatPagerPrefs.saveDefaultOffset(ctx, -120)
        FloatPagerPrefs.saveLongPressDuration(ctx, 600)
        FloatPagerPrefs.clearSavedPosition(ctx)
    }
}
