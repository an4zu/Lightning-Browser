package acr.browser.lightning.display

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

data class FontConfig(
    val fontSize: Int = 16,
    val minimumFontSize: Int = 12,
    val textZoom: Int = 100,
    val fontFamily: String = "sans-serif",
    val fakeBold: Boolean = false,      // 加粗
    val enhanceEdges: Boolean = false   // 防毛刺
)

object FontEngine {

    private const val PREF = "font_config"
    private var config = FontConfig()

    fun load(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        config = FontConfig(
            fontSize = sp.getInt("fontSize", 16),
            minimumFontSize = sp.getInt("minimumFontSize", 12),
            textZoom = sp.getInt("textZoom", 100),
            fontFamily = sp.getString("fontFamily", "sans-serif") ?: "sans-serif",
            fakeBold = sp.getBoolean("fakeBold", false),
            enhanceEdges = sp.getBoolean("enhanceEdges", false)
        )
    }

    fun save(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .putInt("fontSize", config.fontSize)
            .putInt("minimumFontSize", config.minimumFontSize)
            .putInt("textZoom", config.textZoom)
            .putString("fontFamily", config.fontFamily)
            .putBoolean("fakeBold", config.fakeBold)
            .putBoolean("enhanceEdges", config.enhanceEdges)
            .apply()
    }

    fun applyToWebView(webView: WebView) {
        val s = webView.settings

        s.defaultFontSize = config.fontSize
        s.minimumFontSize = config.minimumFontSize
        s.textZoom = config.textZoom

        s.standardFontFamily = config.fontFamily
        s.serifFontFamily = config.fontFamily
        s.sansSerifFontFamily = config.fontFamily

        // 加粗（Fake Bold）
        if (config.fakeBold) {
            webView.evaluateJavascript(
                "document.body.style.fontWeight='600';",
                null
            )
        }

        // 防毛刺（抗锯齿）
        if (config.enhanceEdges) {
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun update(newConfig: FontConfig, context: Context) {
        config = newConfig
        save(context)
    }

    fun current(): FontConfig = config
}
