package acr.browser.lightning.ui.float

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * FloatPager 的所有设置项与持久化逻辑。
 *
 * 包含：
 * - 全局开关
 * - 默认透明度（op）
 * - 临时透明度（hideOp）
 * - 按钮主尺寸（16–96dp）
 * - 默认位置 X/Y
 * - 默认偏移量
 * - 长按关闭时间（10–1500ms）
 * - 拖动后保存的位置
 * - 临时透明度状态
 */
object FloatPagerPrefs {

    private const val KEY_ENABLED = "floatpager_enabled"
    private const val KEY_OPACITY = "floatpager_opacity"
    private const val KEY_HIDE_OPACITY = "floatpager_hide_opacity"
    private const val KEY_MAIN_SIZE = "floatpager_main_size"
    private const val KEY_DEFAULT_X = "floatpager_default_x"
    private const val KEY_DEFAULT_Y = "floatpager_default_y"
    private const val KEY_DEFAULT_OFFSET = "floatpager_default_offset"
    private const val KEY_LONG_PRESS = "floatpager_long_press"

    private const val KEY_SAVED_X = "floatpager_saved_x"
    private const val KEY_SAVED_Y = "floatpager_saved_y"
    private const val KEY_TEMP_FADE = "floatpager_temp_fade"

    private fun prefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    // -----------------------------
    // 全局开关
    // -----------------------------
    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    // -----------------------------
    // 默认透明度（0.1–1.0）
    // -----------------------------
    fun loadOpacity(context: Context): Float =
        prefs(context).getFloat(KEY_OPACITY, 0.4f)

    fun saveOpacity(context: Context, value: Float) {
        prefs(context).edit().putFloat(KEY_OPACITY, value).apply()
    }

    // -----------------------------
    // 临时变淡透明度（0.05–0.5）
    // -----------------------------
    fun loadHideOpacity(context: Context): Float =
        prefs(context).getFloat(KEY_HIDE_OPACITY, 0.1f)

    fun saveHideOpacity(context: Context, value: Float) {
        prefs(context).edit().putFloat(KEY_HIDE_OPACITY, value).apply()
    }

    // -----------------------------
    // 按钮主尺寸（16–96dp）
    // -----------------------------
    fun loadMainButtonSize(context: Context): Int =
        prefs(context).getInt(KEY_MAIN_SIZE, 48)

    fun saveMainButtonSize(context: Context, size: Int) {
        prefs(context).edit().putInt(KEY_MAIN_SIZE, size).apply()
    }

    // -----------------------------
    // 默认位置（X/Y）
    // -----------------------------
    fun loadDefaultX(context: Context): Int =
        prefs(context).getInt(KEY_DEFAULT_X, -1)

    fun saveDefaultX(context: Context, x: Int) {
        prefs(context).edit().putInt(KEY_DEFAULT_X, x).apply()
    }

    fun loadDefaultY(context: Context): Int =
        prefs(context).getInt(KEY_DEFAULT_Y, -1)

    fun saveDefaultY(context: Context, y: Int) {
        prefs(context).edit().putInt(KEY_DEFAULT_Y, y).apply()
    }

    // -----------------------------
    // 默认偏移量（-120dp）
    // -----------------------------
    fun loadDefaultOffset(context: Context): Int =
        prefs(context).getInt(KEY_DEFAULT_OFFSET, -120)

    fun saveDefaultOffset(context: Context, offset: Int) {
        prefs(context).edit().putInt(KEY_DEFAULT_OFFSET, offset).apply()
    }

    // -----------------------------
    // 长按关闭时间（10–1500ms）
    // -----------------------------
    fun loadLongPressDuration(context: Context): Int =
        prefs(context).getInt(KEY_LONG_PRESS, 600)

    fun saveLongPressDuration(context: Context, ms: Int) {
        prefs(context).edit().putInt(KEY_LONG_PRESS, ms).apply()
    }

    // -----------------------------
    // 拖动后保存的位置
    // -----------------------------
    fun loadSavedX(context: Context): Int =
        prefs(context).getInt(KEY_SAVED_X, -1)

    fun loadSavedY(context: Context): Int =
        prefs(context).getInt(KEY_SAVED_Y, -1)

    fun savePosition(context: Context, x: Int, y: Int) {
        prefs(context).edit()
            .putInt(KEY_SAVED_X, x)
            .putInt(KEY_SAVED_Y, y)
            .apply()
    }

    fun clearSavedPosition(context: Context) {
        prefs(context).edit()
            .remove(KEY_SAVED_X)
            .remove(KEY_SAVED_Y)
            .apply()
    }

    // -----------------------------
    // 临时透明度状态（当前页面）
    // -----------------------------
    fun isTempFadeEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_TEMP_FADE, false)

    fun setTempFade(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_TEMP_FADE, enabled).apply()
    }
}

