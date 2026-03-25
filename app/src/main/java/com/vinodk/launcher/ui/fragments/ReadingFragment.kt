package com.vinodk.launcher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vinodk.launcher.databinding.FragmentReadingBinding
import java.text.SimpleDateFormat
import java.util.*

class ReadingFragment : Fragment() {
    private var _b: FragmentReadingBinding? = null
    private val b get() = _b!!

    override fun onCreateView(inf: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentReadingBinding.inflate(inf, c, false); return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val now = Date()
        b.tvReadingTime.text = SimpleDateFormat("h:mm", Locale.getDefault()).format(now)
        b.tvReadingAmpm.text = SimpleDateFormat("a", Locale.getDefault()).format(now)
        b.tvReadingDate.text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now).uppercase()

        // Animate ink progress to 63% after layout pass
        b.inkProgress.post {
            val parent = b.inkProgress.parent as View
            val target = (parent.width * 0.63f).toInt()
            b.inkProgress.layoutParams.width = target
            b.inkProgress.requestLayout()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
