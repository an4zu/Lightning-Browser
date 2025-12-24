package acr.browser.lightning.browser.floatpager

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

/**
 * FloatPager：悬浮翻页器核心逻辑（最终补丁版 + 性能优化）
 *
 * 新增内容（补丁）：
 * ----------------------------------------------------
 * ✔ refresh()：刷新后重置位置 + 更新设置
 * ✔ applySettings()：设置变化后刷新 UI
 * ✔ updateOpacity()：透明度变化后更新
 * ✔ updateButtonSizes()：按钮尺寸变化后更新
 * ✔ 节流 updateViewLayout()，减少 CPU 占用、拖动更流畅
 * ----------------------------------------------------
 */
class FloatPager(
    private val activity: Activity,
    private val onPageUp: () -> Unit,
    private val onPageDown: () -> Unit,
    private val onCloseTab: () -> Unit
) {

    private val wm = activity.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
    private val view = FloatPagerView(activity)

    private val handler = Handler(Looper.getMainLooper())
    private var longPressTriggered = false

    private val params = WindowManager.LayoutParams().apply {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.START
        format = PixelFormat.TRANSLUCENT
        flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
    }

    private var lastX = 0f
    private var lastY = 0f
    private var isDragging = false

    // ⭐ 节流标记：避免高频 updateViewLayout()
    private var pendingUpdate = false

    // ⭐ 节流后的 updateViewLayout()
    private fun requestUpdate() {
        if (pendingUpdate) return
        pendingUpdate = true

        handler.post {
            wm.updateViewLayout(view, params)
            pendingUpdate = false
        }
    }

    // ----------------------------------------------------
    // 初始化
    // ----------------------------------------------------
    fun show() {
        if (!FloatPagerPrefs.isEnabled(activity)) return

        loadInitialPosition()
        updateOpacity()
        updateButtonSizes()

        setupButtons()
        setupDrag()

        wm.addView(view, params)
    }

    fun remove() {
        runCatching { wm.removeView(view) }
    }

    // ----------------------------------------------------
    // ⭐ 刷新后调用（供 BrowserActivity 使用）
    // ----------------------------------------------------
    fun refresh() {
        resetPositionOnRefresh()
        applySettings()
    }

    // ----------------------------------------------------
    // ⭐ 设置变化后刷新 UI
    // ----------------------------------------------------
    fun applySettings() {
        updateOpacity()
        updateButtonSizes()
        wm.updateViewLayout(view, params)
    }

    // ----------------------------------------------------
    // ⭐ 透明度更新
    // ----------------------------------------------------
    private fun updateOpacity() {
        val op = FloatPagerPrefs.loadOpacity(activity)
        val hideOp = FloatPagerPrefs.loadHideOpacity(activity)
        val finalAlpha =
            if (FloatPagerPrefs.isTempFadeEnabled(activity)) hideOp else op
        view.alpha = finalAlpha
    }

    // ----------------------------------------------------
    // ⭐ 按钮尺寸更新
    // ----------------------------------------------------
    private fun updateButtonSizes() {
        val mainSize = FloatPagerPrefs.loadMainButtonSize(activity)
        val smallSize = mainSize / 3

        view.btnUp.layoutParams.width = mainSize
        view.btnUp.layoutParams.height = mainSize

        view.btnDown.layoutParams.width = mainSize
        view.btnDown.layoutParams.height = mainSize

        view.btnTransparent.layoutParams.width = smallSize
        view.btnTransparent.layoutParams.height = smallSize

        view.btnCenter.layoutParams.width = smallSize
        view.btnCenter.layoutParams.height = smallSize

        view.requestLayout()
    }

    // ----------------------------------------------------
    // 加载初始位置（默认位置 or 保存位置）
    // ----------------------------------------------------
    private fun loadInitialPosition() {
        val savedX = FloatPagerPrefs.loadSavedX(activity)
        val savedY = FloatPagerPrefs.loadSavedY(activity)

        if (savedX != -1 && savedY != -1) {
            params.x = savedX
            params.y = savedY
            return
        }

        val dm = activity.resources.displayMetrics
        val defaultX = FloatPagerPrefs.loadDefaultX(activity)
        val defaultY = FloatPagerPrefs.loadDefaultY(activity)
        val offset = FloatPagerPrefs.loadDefaultOffset(activity)

        params.x = if (defaultX == -1) (dm.widthPixels * 0.8f).toInt() else defaultX
        params.y = if (defaultY == -1) (dm.heightPixels * 0.8f).toInt() else defaultY
        params.y += offset
    }

    // ----------------------------------------------------
    // 刷新时重置位置
    // ----------------------------------------------------
    fun resetPositionOnRefresh() {
        FloatPagerPrefs.clearSavedPosition(activity)
        loadInitialPosition()
        wm.updateViewLayout(view, params)
    }

    // ----------------------------------------------------
    // 按钮事件
    // ----------------------------------------------------
    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtons() {

        view.btnUp.setOnClickListener { onPageUp() }
        view.btnDown.setOnClickListener { onPageDown() }

        view.btnTransparent.setOnClickListener {
            val current = FloatPagerPrefs.isTempFadeEnabled(activity)
            FloatPagerPrefs.setTempFade(activity, !current)
            updateOpacity()
        }

        view.btnCenter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    longPressTriggered = false
                    handler.postDelayed({
                        longPressTriggered = true
                        onCloseTab()
                    }, FloatPagerPrefs.loadLongPressDuration(activity).toLong())
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacksAndMessages(null)
                }
            }
            true
        }
    }

    // ----------------------------------------------------
    // 拖动逻辑（已优化）
    // ----------------------------------------------------
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrag() {
        view.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX
                    lastY = event.rawY
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - lastX
                    val dy = event.rawY - lastY

                    if (!isDragging && (dx * dx + dy * dy) > 16) {
                        isDragging = true
                    }

                    if (isDragging) {
                        params.x += dx.toInt()
                        params.y += dy.toInt()

                        val dm = activity.resources.displayMetrics
                        params.x = max(0, min(params.x, dm.widthPixels))
                        params.y = max(0, min(params.y, dm.heightPixels))

                        // ⭐ 使用节流更新，避免高频 updateViewLayout 卡顿
                        requestUpdate()
                    }

                    lastX = event.rawX
                    lastY = event.rawY
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        FloatPagerPrefs.savePosition(activity, params.x, params.y)
                    }
                }
            }
            false
        }
    }
}
