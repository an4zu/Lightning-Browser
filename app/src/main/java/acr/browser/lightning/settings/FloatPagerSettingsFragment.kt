package acr.browser.lightning.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import acr.browser.lightning.browser.floatpager.FloatPagerPrefs
import acr.browser.lightning.R

/**
 * 网页悬浮翻页器（vz112 版）设置界面。
 * Lite 版本需要保留此功能，因此本文件必须存在。
 */
class FloatPagerSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.float_pager_prefs, rootKey)

        val ctx = requireContext()

        // 透明度
        findPreference<SeekBarPreference>("floatpager_opacity")?.apply {
            value = (FloatPagerPrefs.loadOpacity(ctx) * 100).toInt()
            summary = "当前：${FloatPagerPrefs.loadOpacity(ctx)}"
            setOnPreferenceChangeListener { _, newValue ->
                val v = (newValue as Int) / 100f
                FloatPagerPrefs.saveOpacity(ctx, v)
                summary = "当前：$v"
                true
            }
        }

        // 隐藏透明度
        findPreference<SeekBarPreference>("floatpager_hide_opacity")?.apply {
            value = (FloatPagerPrefs.loadHideOpacity(ctx) * 100).toInt()
            summary = "当前：${FloatPagerPrefs.loadHideOpacity(ctx)}"
            setOnPreferenceChangeListener { _, newValue ->
                val v = (newValue as Int) / 100f
                FloatPagerPrefs.saveHideOpacity(ctx, v)
                summary = "当前：$v"
                true
            }
        }

        // 主按钮尺寸
        findPreference<SeekBarPreference>("floatpager_main_size")?.apply {
            value = FloatPagerPrefs.loadMainButtonSize(ctx)
            summary = "当前：${FloatPagerPrefs.loadMainButtonSize(ctx)} dp"
            setOnPreferenceChangeListener { _, newValue ->
                val v = newValue as Int
                FloatPagerPrefs.saveMainButtonSize(ctx, v)
                summary = "当前：${v} dp"
                true
            }
        }

        // 重置位置
        findPreference<Preference>("floatpager_reset_position")?.setOnPreferenceClickListener {
            FloatPagerPrefs.clearSavedPosition(ctx)
            true
        }

        // 恢复默认
        findPreference<Preference>("floatpager_restore_defaults")?.setOnPreferenceClickListener {
            FloatPagerPrefs.setEnabled(ctx, true)
            FloatPagerPrefs.saveOpacity(ctx, 0.4f)
            FloatPagerPrefs.saveHideOpacity(ctx, 0.1f)
            FloatPagerPrefs.saveMainButtonSize(ctx, 48)
            FloatPagerPrefs.saveDefaultX(ctx, -1)
            FloatPagerPrefs.saveDefaultY(ctx, -1)
            FloatPagerPrefs.saveDefaultOffset(ctx, -120)
            FloatPagerPrefs.saveLongPressDuration(ctx, 600)
            FloatPagerPrefs.clearSavedPosition(ctx)
            true
        }
    }
}
