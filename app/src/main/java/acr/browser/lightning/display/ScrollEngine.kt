package acr.browser.lightning.display

import android.webkit.WebView

enum class ScrollMode {
    CONTENT_RATIO,   // 原始比例
    VIEWPORT_RATIO   // 视口比例
}

data class ScrollConfig(
    val mode: ScrollMode = ScrollMode.CONTENT_RATIO,
    val stepPercent: Float = 0.9f
)

object ScrollEngine {

    fun scroll(webView: WebView, config: ScrollConfig, forward: Boolean) {
        val percent = if (forward) config.stepPercent else -config.stepPercent

        when (config.mode) {
            ScrollMode.CONTENT_RATIO -> scrollByContentRatio(webView, percent)
            ScrollMode.VIEWPORT_RATIO -> scrollByViewportRatio(webView, percent)
        }

        // E‑Ink 轻量刷新
        webView.invalidate()
    }

    private fun scrollByContentRatio(webView: WebView, percent: Float) {
        val contentHeight = (webView.contentHeight * webView.scale).toInt()
        val dy = (contentHeight * percent).toInt()
        webView.scrollBy(0, dy)
    }

    private fun scrollByViewportRatio(webView: WebView, percent: Float) {
        val dy = (webView.height * percent).toInt()
        webView.scrollBy(0, dy)
    }
}
