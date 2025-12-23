package acr.browser.lightning.display

import android.webkit.WebView

object ReflowEngine {

    // 强制重排（不闪、不跳、不 reload）
    fun forceReflow(webView: WebView) {
        val js = """
            document.body.style.width = (document.body.clientWidth - 1) + 'px';
            setTimeout(() => { document.body.style.width = '100%'; }, 0);
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }
}
