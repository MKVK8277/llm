package com.vinodk.launcher.ui.fragments

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vinodk.launcher.databinding.FragmentHomeBinding
import com.vinodk.launcher.models.AppInfo
import com.vinodk.launcher.ui.adapters.AppGridAdapter
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!

    private val handler = Handler(Looper.getMainLooper())

    // FIX #8: Use handler.post instead of direct .run() call
    private val clockTick = object : Runnable {
        override fun run() {
            if (_b == null) return
            tick()
            val nextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            handler.postDelayed(this, nextMinute)
        }
    }

    private val pkgReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, i: Intent?) = loadApps()
    }

    override fun onCreateView(inf: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHomeBinding.inflate(inf, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)

        // Editorial -10dp clock offset matching HTML --offset-x: -10px
        b.tvClock.translationX = -10f * resources.displayMetrics.density

        // FIX #4 (grid): Set GridLayoutManager programmatically to guarantee spanCount=4
        b.rvAppGrid.layoutManager = GridLayoutManager(requireContext(), 4)

        // FIX #8: Post via handler, never call .run() directly
        handler.post(clockTick)

        loadApps()

        // Listen for app installs/removals to refresh grid
        val f = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        requireContext().registerReceiver(pkgReceiver, f)
    }

    private fun tick() {
        val now = Date()
        b.tvClock.text = SimpleDateFormat("h:mm", Locale.getDefault()).format(now)

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val sal = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
        val prefs = requireContext().getSharedPreferences("stitch", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "Vinod") ?: "Vinod"
        b.tvGreeting.text = "$sal, $name".uppercase()
        b.tvDate.text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now).uppercase()

        // FIX #3: Use ActivityManager for real device RAM, not JVM heap
        val am = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val freeGb = memInfo.availMem / (1024f * 1024f * 1024f)
        b.tvRam.text = if (freeGb >= 1f) "%.1f GB FREE".format(freeGb)
                       else "${memInfo.availMem / (1024 * 1024)} MB FREE"
    }

    private fun loadApps() {
        val pm = requireContext().packageManager
        val apps = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.GET_META_DATA
        ).map {
            AppInfo(
                label       = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                icon        = it.loadIcon(pm),
                launchIntent = pm.getLaunchIntentForPackage(it.activityInfo.packageName)
            )
        }.sortedBy { it.label }

        b.rvAppGrid.adapter = AppGridAdapter(apps) { app ->
            app.launchIntent?.let { startActivity(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(clockTick)
        try { requireContext().unregisterReceiver(pkgReceiver) } catch (_: Exception) {}
        _b = null
    }
}
