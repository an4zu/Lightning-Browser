package acr.browser.lightning.display

import android.webkit.WebView

/**
 * ReflowEngine
 *
 * 用于触发 WebView 的轻量级重排（reflow），
 * 不会闪屏、不 reload、不破坏布局。
 *
 * 原理：通过微调 body 宽度，迫使浏览器重新计算布局。
 */
object ReflowEngine {

    /**
     * 强制重排（Force Reflow）
     *
     * 适用于：
     * - 字体变化后布局未刷新
     * - 缩放后文字未重新换行
     * - E‑Ink 上页面未正确重排
     */
    fun forceReflow(webView: WebView) {
        val js = """
            (function() {
                const body = document.body;
                if (!body) return;
                const w = body.clientWidth;
                body.style.width = (w - 1) + 'px';
                setTimeout(() => { body.style.width = w + 'px'; }, 0);
            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }
}
