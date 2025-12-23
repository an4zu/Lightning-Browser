package acr.browser.lightning.display

import android.content.Context

object ScrollPrefs {

    private const val PREF = "scroll_config"

    private var config = ScrollConfig()

    fun load(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        config = ScrollConfig(
            mode = if (sp.getString("mode", "CONTENT") == "CONTENT")
                ScrollMode.CONTENT_RATIO else ScrollMode.VIEWPORT_RATIO,
            stepPercent = sp.getFloat("stepPercent", 0.9f)
        )
    }

    fun save(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .putString("mode", if (config.mode == ScrollMode.CONTENT_RATIO) "CONTENT" else "VIEWPORT")
            .putFloat("stepPercent", config.stepPercent)
            .apply()
    }

    fun update(newConfig: ScrollConfig, context: Context) {
        config = newConfig
        save(context)
    }

    fun current(): ScrollConfig = config
}
