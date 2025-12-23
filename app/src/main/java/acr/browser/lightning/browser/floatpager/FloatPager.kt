package acr.browser.lightning.browser.floatpager

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

/**
 * FloatPager：悬浮翻页器核心逻辑
 *
 * 功能：
 * - 拖动保存位置（跨页面继承）
 * - 刷新重置位置（清除保存位置 → 回到默认位置）
 * - 默认透明度（op）
 * - 临时变淡透明度（hideOp）
 * - 临时状态不跨页面、不跨刷新
 * - 长按关闭（10–1500ms）
 * - 上/下翻页
 * - 透明度切换
 * - 全局开关
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

    // -----------------------------
    // 初始化
    // -----------------------------
    fun show() {
        if (!FloatPagerPrefs.isEnabled(activity)) return

        loadInitialPosition()
        applyOpacity()

        setupButtons()
        setupDrag()

        wm.addView(view, params)
    }

    fun remove() {
        runCatching { wm.removeView(view) }
    }

    // -----------------------------
    // 加载初始位置（默认位置 or 保存位置）
    // -----------------------------
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

    // -----------------------------
    // 刷新时重置位置
    // -----------------------------
    fun resetPositionOnRefresh() {
        FloatPagerPrefs.clearSavedPosition(activity)
        loadInitialPosition()
        wm.updateViewLayout(view, params)
    }

    // -----------------------------
    // 透明度逻辑
    // -----------------------------
    private fun applyOpacity() {
        val op = FloatPagerPrefs.loadOpacity(activity)
        val hideOp = FloatPagerPrefs.loadHideOpacity(activity)

        val finalAlpha =
            if (FloatPagerPrefs.isTempFadeEnabled(activity)) hideOp else op

        view.alpha = finalAlpha
    }

    private fun toggleFade() {
        val current = FloatPagerPrefs.isTempFadeEnabled(activity)
        FloatPagerPrefs.setTempFade(activity, !current)
        applyOpacity()
    }

    // -----------------------------
    // 按钮事件
    // -----------------------------
    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtons() {

        // 上翻页
        view.btnUp.setOnClickListener {
            onPageUp()
        }

        // 下翻页
        view.btnDown.setOnClickListener {
            onPageDown()
        }

        // 透明度切换
        view.btnTransparent.setOnClickListener {
            toggleFade()
        }

        // 中间按钮：长按关闭
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

    // -----------------------------
    // 拖动逻辑
    // -----------------------------
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

                        wm.updateViewLayout(view, params)
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
