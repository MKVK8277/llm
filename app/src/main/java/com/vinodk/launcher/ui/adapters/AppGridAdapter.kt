package com.vinodk.launcher.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vinodk.launcher.databinding.ItemAppBinding
import com.vinodk.launcher.models.AppInfo

class AppGridAdapter(
    private val apps: List<AppInfo>,
    private val onClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppGridAdapter.VH>() {

    inner class VH(val b: ItemAppBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val app = apps[pos]
        h.b.ivAppIcon.setImageDrawable(app.icon)
        h.b.tvAppLabel.text = app.label
        h.itemView.setOnClickListener { onClick(app) }
    }

    override fun getItemCount() = apps.size
}
