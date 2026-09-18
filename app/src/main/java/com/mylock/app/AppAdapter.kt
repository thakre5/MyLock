package com.mylock.app

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

data class AppItem(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable,
    var isLocked: Boolean
)

class AppAdapter(
    private val context: Context,
    private val appList: List<AppItem>
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val prefs = context.getSharedPreferences("MyLockPrefs", Context.MODE_PRIVATE)

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgAppIcon)
        val tvName: TextView = view.findViewById(R.id.tvAppName)
        val switchLock: SwitchCompat = view.findViewById(R.id.switchLock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.tvName.text = app.appName
        holder.imgIcon.setImageDrawable(app.appIcon)
        
        // Clear listener first to avoid recycling bugs
        holder.switchLock.setOnCheckedChangeListener(null)
        holder.switchLock.isChecked = app.isLocked

        holder.switchLock.setOnCheckedChangeListener { _, isChecked ->
            app.isLocked = isChecked
            prefs.edit().putBoolean("lock_${app.packageName}", isChecked).apply()
        }
    }

    override fun getItemCount() = appList.size
}
