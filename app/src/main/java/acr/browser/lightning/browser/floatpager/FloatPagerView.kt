package acr.browser.lightning.browser.floatpager

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * 悬浮翻页器的 UI 视图：
 * - 顶部透明度按钮（主尺寸 1/3）
 * - 上翻页按钮（主尺寸）
 * - 中间关闭按钮（主尺寸 1/3）
 * - 下翻页按钮（主尺寸）
 *
 * 按钮尺寸由 FloatPagerPrefs 提供：
 * - mainSize：主按钮尺寸（上 / 下）
 * - smallSize：mainSize / 3（透明度 + 中间关闭）
 */
class FloatPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val btnTransparent: ImageView
    val btnUp: ImageView
    val btnCenter: ImageView
    val btnDown: ImageView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        val mainSize = FloatPagerPrefs.loadMainButtonSize(context)
        val smallSize = mainSize / 3

        btnTransparent = ImageView(context).apply {
            layoutParams = LayoutParams(smallSize, smallSize)
            setPadding(4, 4, 4, 4)
            // 这里先用系统图标占位，后续你可以替换成项目自己的资源
            setImageResource(android.R.drawable.presence_invisible)
        }

        btnUp = ImageView(context).apply {
            layoutParams = LayoutParams(mainSize, mainSize)
            setPadding(4, 4, 4, 4)
            setImageResource(android.R.drawable.arrow_up_float)
        }

        btnCenter = ImageView(context).apply {
            layoutParams = LayoutParams(smallSize, smallSize)
            setPadding(4, 4, 4, 4)
            setImageResource(android.R.drawable.ic_delete)
        }

        btnDown = ImageView(context).apply {
            layoutParams = LayoutParams(mainSize, mainSize)
            setPadding(4, 4, 4, 4)
            setImageResource(android.R.drawable.arrow_down_float)
        }

        addView(btnTransparent)
        addView(btnUp)
        addView(btnCenter)
        addView(btnDown)
    }
}

