package acr.browser.lightning.settings.activity

import acr.browser.lightning.R
import acr.browser.lightning.settings.fragment.RootSettingsFragment
import acr.browser.lightning.display.FontEngine
import acr.browser.lightning.display.FontConfig
import acr.browser.lightning.display.ScrollConfig
import acr.browser.lightning.display.ScrollMode
import acr.browser.lightning.display.ScrollPrefs

import android.os.Bundle
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Switch
import android.widget.RadioGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : ThemableSettingsActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_root)

        // 加载当前配置
        val cfg = FontEngine.current()
        val scrollCfg = ScrollPrefs.current()

        // 绑定 UI 控件
        val fontSizeSeek = findViewById<SeekBar>(R.id.fontSizeSeek)
        val minFontSizeSeek = findViewById<SeekBar>(R.id.minFontSizeSeek)
        val switchFakeBold = findViewById<Switch>(R.id.switchFakeBold)
        val switchHeavyBold = findViewById<Switch>(R.id.switchHeavyBold)
        val switchEnhanceEdges = findViewById<Switch>(R.id.switchEnhanceEdges)
        val scrollGroup = findViewById<RadioGroup>(R.id.scrollModeGroup)

        // 初始化 UI 状态
        fontSizeSeek.progress = cfg.fontSize
        minFontSizeSeek.progress = cfg.minimumFontSize
        switchFakeBold.isChecked = cfg.fakeBold
        switchHeavyBold.isChecked = cfg.heavyBold
        switchEnhanceEdges.isChecked = cfg.enhanceEdges

        scrollGroup.check(
            if (scrollCfg.mode == ScrollMode.CONTENT_RATIO)
                R.id.scrollContentRatio else R.id.scrollViewportRatio
        )

        // 字体大小
        fontSizeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                FontEngine.update(cfg.copy(fontSize = value), this@SettingsActivity)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // 最小字体
        minFontSizeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                FontEngine.update(cfg.copy(minimumFontSize = value), this@SettingsActivity)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // 加粗
        switchFakeBold.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(fakeBold = checked), this@SettingsActivity)
        }

        // 加黑
        switchHeavyBold.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(heavyBold = checked), this@SettingsActivity)
        }

        // 防毛刺
        switchEnhanceEdges.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(enhanceEdges = checked), this@SettingsActivity)
        }

        // 滚动模式
        scrollGroup.setOnCheckedChangeListener { _, id ->
            val mode = if (id == R.id.scrollContentRatio)
                ScrollMode.CONTENT_RATIO else ScrollMode.VIEWPORT_RATIO

            val newCfg = scrollCfg.copy(mode = mode)
            ScrollPrefs.update(newCfg, this@SettingsActivity)
        }

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 加载设置 Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root, RootSettingsFragment())
            .commit()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_from_right,
                R.anim.fade_out_scale,
                R.anim.fade_in_scale,
                R.anim.slide_out_to_right
            )
            .replace(R.id.root, fragment)
            .addToBackStack(null)
            .commit()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}
