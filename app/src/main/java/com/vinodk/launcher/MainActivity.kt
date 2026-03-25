package com.vinodk.launcher

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.vinodk.launcher.databinding.ActivityMainBinding
import com.vinodk.launcher.ui.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val tabs get() = listOf(b.tabSetup, b.tabHome, b.tabReading, b.tabSettings)

    override fun onCreate(savedInstanceState: Bundle?) {
        // FIX #2 & #9: Apply saved theme BEFORE super.onCreate
        val prefs = getSharedPreferences("stitch", Context.MODE_PRIVATE)
        when (prefs.getString("theme", "papyrus")) {
            "sepia"    -> setTheme(R.style.Theme_StitchLauncher_Sepia)
            "charcoal" -> setTheme(R.style.Theme_StitchLauncher_Charcoal)
            else       -> setTheme(R.style.Theme_StitchLauncher)
        }

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.tabSetup.setOnClickListener    { switchTab(it as Button, SetupFragment()) }
        b.tabHome.setOnClickListener     { switchTab(it as Button, HomeFragment()) }
        b.tabReading.setOnClickListener  { switchTab(it as Button, ReadingFragment()) }
        b.tabSettings.setOnClickListener { switchTab(it as Button, SettingsFragment()) }

        if (savedInstanceState == null) switchTab(b.tabHome, HomeFragment())
    }

    // Public so SetupFragment can call it after name entry
    fun switchTab(active: Button, fragment: Fragment) {
        // FIX #1 & #7: Use background = null + setBackgroundColor to properly clear
        // Material TextButton background is driven by backgroundTintList not background drawable
        tabs.forEach { btn ->
            btn.background = null
            btn.setTextColor(getColor(R.color.papyrus_secondary))
            btn.elevation = 0f
        }
        // FIX #1: Set active tab bg via background drawable (not backgroundTint)
        active.setBackgroundResource(R.drawable.bg_tab_active)
        active.setTextColor(getColor(R.color.papyrus_primary))
        active.elevation = 4f

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Convenience overload used by SetupFragment (passes button reference by tab position)
    fun switchToHome() = switchTab(b.tabHome, HomeFragment())

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { /* swallow — launcher behaviour */ }
}
