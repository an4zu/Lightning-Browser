package acr.browser.lightning

import acr.browser.lightning.browser.BrowserActivity
import acr.browser.lightning.display.ScrollPrefs

/**
 * The default browsing experience.
 */
class DefaultBrowserActivity : BrowserActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // 加载滚动配置（CONTENT_RATIO / VIEWPORT_RATIO）
        ScrollPrefs.load(this)
    }

    override fun isIncognito(): Boolean = false

    override fun menu(): Int = R.menu.main

    override fun homeIcon(): Int = R.drawable.ic_action_home
}
