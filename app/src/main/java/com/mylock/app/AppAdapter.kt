package com.mylock.app

content class AppItem(
    val appName: String,
    val packageName: String,
    val appIcon: android.graphics.drawable.Drawable,
    var isLocked: Boolean
)

class AppAdapter(
    private val context: android.content.Context,
    private val appList: List<AppItem>
) : androidx.recyclerview.widget.RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val prefs = context.getSharedPreferences("MyLockPrefs", android.content.Context.MODE_PRIVATE)

    class AppViewHolder(view: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val imgIcon: android.widget.ImageView = view.findViewById(R.id.imgAppIcon)
        val tvName: android.widget.TextView = view.findViewById(R.id.tvAppName)
        val switchLock: androidx.appcompat.widget.SwitchCompat = view.findViewById(R.id.switchLock)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): AppViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.tvName.text = app.appName
        holder.imgIcon.setImageDrawable(app.appIcon)
        holder.switchLock.isChecked = app.isLocked

        holder.switchLock.setOnbuttonChangeListener(null) // Prevent glitching during recycling
        holder.switchLock.isChecked = app.isLocked

        holder.switchLock.setOnCheckedChangeListener { _, isChecked ->
            app.isLocked = isChecked
            prefs.edit().putBoolean("lock_${app.packageName}", isChecked).apply()
        }
    }

    override fun getItemCount() = appList.size
}
