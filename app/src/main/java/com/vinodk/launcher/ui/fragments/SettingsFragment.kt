package com.vinodk.launcher.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.vinodk.launcher.R
import com.vinodk.launcher.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(inf: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSettingsBinding.inflate(inf, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val prefs = requireContext().getSharedPreferences("stitch", Context.MODE_PRIVATE)

        // Load saved values
        b.etUserName.setText(prefs.getString("user_name", "Vinod Kumar"))
        b.etLocation.setText(prefs.getString("location", "London, UK"))
        b.switchRam.isChecked   = prefs.getBoolean("ram_monitor", true)
        b.switchFocus.isChecked = prefs.getBoolean("focus_gate", false)

        // FIX #6: Save profile edits on every keystroke — no separate save button needed
        b.etUserName.addTextChangedListener { text ->
            prefs.edit().putString("user_name", text.toString().trim()).apply()
        }
        b.etLocation.addTextChangedListener { text ->
            prefs.edit().putString("location", text.toString().trim()).apply()
        }

        // Toggle listeners
        b.switchRam.setOnCheckedChangeListener { _, v ->
            prefs.edit().putBoolean("ram_monitor", v).apply()
        }
        b.switchFocus.setOnCheckedChangeListener { _, v ->
            prefs.edit().putBoolean("focus_gate", v).apply()
        }

        // Theme radio — restore saved state
        when (prefs.getString("theme", "papyrus")) {
            "sepia"    -> b.rgTheme.check(R.id.rb_sepia)
            "charcoal" -> b.rgTheme.check(R.id.rb_charcoal)
            else       -> b.rgTheme.check(R.id.rb_papyrus)
        }

        // FIX #6 & #9: Save theme then recreate — theme is read in MainActivity.onCreate
        b.rgTheme.setOnCheckedChangeListener { _, id ->
            val theme = when (id) {
                R.id.rb_sepia    -> "sepia"
                R.id.rb_charcoal -> "charcoal"
                else             -> "papyrus"
            }
            prefs.edit().putString("theme", theme).apply()
            activity?.recreate() // MainActivity.onCreate now reads this before setTheme()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
