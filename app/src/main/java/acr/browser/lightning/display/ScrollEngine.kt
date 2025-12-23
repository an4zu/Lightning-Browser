package acr.browser.lightning.display

import android.webkit.WebView

enum class ScrollMode {
    CONTENT_RATIO,   // 按内容高度比例滚动（原始比例）
    VIEWPORT_RATIO   // 按视口高度比例滚动（翻页 / 阅读模式）
}

data class ScrollConfig(
    val mode: ScrollMode = ScrollMode.CONTENT_RATIO,
    // 每次滚动比例，0.9 表示每次滚动 90% 屏高 / 内容高
    val stepPercent: Float = 0.9f
)

object ScrollEngine {

    /**
     * 统一滚动入口
     *
     * @param webView 目标 WebView
     * @param config  滚动配置（模式 + 步长）
     * @param forward true 向下滚动 / false 向上滚动
     */
    fun scroll(webView: WebView, config: ScrollConfig, forward: Boolean) {
        if (config.stepPercent <= 0f) return

        val percent = if (forward) config.stepPercent else -config.stepPercent

        when (config.mode) {
            ScrollMode.CONTENT_RATIO -> scrollByContentRatio(webView, percent)
            ScrollMode.VIEWPORT_RATIO -> scrollByViewportRatio(webView, percent)
        }

        // E‑Ink 轻量刷新，避免拖影
        webView.invalidate()
    }

    /**
     * 按内容高度比例滚动
     * 不受当前视口高度影响，更接近「按全文比例翻页」
     */
    private fun scrollByContentRatio(webView: WebView, percent: Float) {
        // contentHeight 是以 CSS 像素计，需要乘以 scale 才是实际 px
        val contentHeightPx = (webView.contentHeight * webView.scale).toInt()
        if (contentHeightPx <= 0) return

        val dy = (contentHeightPx * percent).toInt()
        if (dy == 0) return

        webView.scrollBy(0, dy)
    }

    /**
     * 按视口高度比例滚动
     * 更接近「按屏翻页」，阅读体验更自然
     */
    private fun scrollByViewportRatio(webView: WebView, percent: Float) {
        val viewHeight = webView.height
        if (viewHeight <= 0) return

        val dy = (viewHeight * percent).toInt()
        if (dy == 0) return

        webView.scrollBy(0, dy)
    }
}
