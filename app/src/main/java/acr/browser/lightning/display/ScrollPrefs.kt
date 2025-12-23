package acr.browser.lightning.display

import android.content.Context

/**
 * ScrollPrefs
 *
 * 负责 ScrollConfig 的持久化：
 * - 滚动模式（内容比例 / 视口比例）
 * - 滚动步长（stepPercent）
 */
object ScrollPrefs {

    private const val PREF = "scroll_config"

    // 内存中的当前配置
    private var config: ScrollConfig = ScrollConfig()

    /**
     * 从 SharedPreferences 中加载配置。
     * 建议在 Application 或主 Activity 的 onCreate 中调用一次。
     */
    fun load(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val modeString = sp.getString("mode", "CONTENT") ?: "CONTENT"
        val mode = if (modeString == "CONTENT") {
            ScrollMode.CONTENT_RATIO
        } else {
            ScrollMode.VIEWPORT_RATIO
        }

        val step = sp.getFloat("stepPercent", 0.9f)

        config = ScrollConfig(
            mode = mode,
            stepPercent = step
        )
    }

    /**
     * 将当前配置写入 SharedPreferences。
     */
    fun save(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .putString(
                "mode",
                if (config.mode == ScrollMode.CONTENT_RATIO) "CONTENT" else "VIEWPORT"
            )
            .putFloat("stepPercent", config.stepPercent)
            .apply()
    }

    /**
     * 更新配置并立即持久化。
     */
    fun update(newConfig: ScrollConfig, context: Context) {
        config = newConfig
        save(context)
    }

    /**
     * 获取当前的滚动配置（内存态）。
     */
    fun current(): ScrollConfig = config
}
