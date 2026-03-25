package com.vinodk.launcher.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.vinodk.launcher.MainActivity
import com.vinodk.launcher.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private var _b: FragmentSetupBinding? = null
    private val b get() = _b!!

    override fun onCreateView(inf: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSetupBinding.inflate(inf, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val prefs = requireContext().getSharedPreferences("stitch", Context.MODE_PRIVATE)
        b.etName.setText(prefs.getString("user_name", ""))

        b.btnContinue.setOnClickListener { save() }
        b.etName.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) { save(); true } else false
        }
    }

    private fun save() {
        val name = b.etName.text.toString().trim()
        if (name.isNotEmpty()) {
            requireContext().getSharedPreferences("stitch", Context.MODE_PRIVATE)
                .edit().putString("user_name", name).apply()

            // Dismiss keyboard
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(b.etName.windowToken, 0)

            // FIX #5: Go through MainActivity so the HOME tab gets properly highlighted
            (activity as? MainActivity)?.switchToHome()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
