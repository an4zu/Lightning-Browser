/*
 * Copyright 2014 A.C.R. Development
 */
package acr.browser.lightning.settings.activity

import acr.browser.lightning.R
import acr.browser.lightning.settings.fragment.RootSettingsFragment
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : ThemableSettingsActivity(),
import acr.browser.lightning.display.FontEngine
import acr.browser.lightning.display.FontConfig
import acr.browser.lightning.display.ScrollConfig
import acr.browser.lightning.display.ScrollMode

    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_root)
        val cfg = FontEngine.current()
        val scrollCfg = ScrollConfig()

        val fontSizeSeek = findViewById<SeekBar>(R.id.fontSizeSeek)
        val minFontSizeSeek = findViewById<SeekBar>(R.id.minFontSizeSeek)
        val switchFakeBold = findViewById<Switch>(R.id.switchFakeBold)
        val switchHeavyBold = findViewById<Switch>(R.id.switchHeavyBold)
        val switchEnhanceEdges = findViewById<Switch>(R.id.switchEnhanceEdges)
        val scrollGroup = findViewById<RadioGroup>(R.id.scrollModeGroup)

        fontSizeSeek.progress = cfg.fontSize
        minFontSizeSeek.progress = cfg.minimumFontSize
        switchFakeBold.isChecked = cfg.fakeBold
        switchHeavyBold.isChecked = cfg.heavyBold
        switchEnhanceEdges.isChecked = cfg.enhanceEdges

        scrollGroup.check(
            if (scrollCfg.mode == ScrollMode.CONTENT_RATIO)
                R.id.scrollContentRatio else R.id.scrollViewportRatio
        )

        fontSizeSeek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                FontEngine.update(cfg.copy(fontSize = value), this@SettingsActivity)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        minFontSizeSeek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                FontEngine.update(cfg.copy(minimumFontSize = value), this@SettingsActivity)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        switchFakeBold.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(fakeBold = checked), this@SettingsActivity)
        }

        switchHeavyBold.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(heavyBold = checked), this@SettingsActivity)
        }

        switchEnhanceEdges.setOnCheckedChangeListener { _, checked ->
            FontEngine.update(cfg.copy(enhanceEdges = checked), this@SettingsActivity)
        }

        scrollGroup.setOnCheckedChangeListener { _, id ->
            val newCfg = ScrollPrefs.current().copy(mode = mode)
            ScrollPrefs.update(newCfg, this@SettingsActivity)

            val mode = if (id == R.id.scrollContentRatio)
                ScrollMode.CONTENT_RATIO else ScrollMode.VIEWPORT_RATIO
            // 保存滚动模式（你可以扩展 ScrollEngine 保存逻辑）
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root, RootSettingsFragment())
            .commit()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
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
