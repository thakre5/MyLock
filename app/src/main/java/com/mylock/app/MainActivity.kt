package com.mylock.app

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPermissions = findViewById<Button>(R.id.btnPermissions)
        btnPermissions.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        // Optional: If you added a settings button to activity_main.xml, handle it here
        /*
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        */
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            Toast.makeText(this, "Permissions Active! Loading Apps...", Toast.LENGTH_SHORT).show()
            startService(Intent(this, MonitorService::class.java))
            loadInstalledApps()
        } else {
            Toast.makeText(this, "Please grant Usage Access permission to enable MyLock", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadInstalledApps() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppItem>()
        val prefs = getSharedPreferences("MyLockPrefs", Context.MODE_PRIVATE)

        for (appInfo in packages) {
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                val appName = pm.getApplicationLabel(appInfo).toString()
                val icon = pm.getApplicationIcon(appInfo)
                val isLocked = prefs.getBoolean("lock_${appInfo.packageName}", false)

                if (appInfo.packageName != packageName) {
                    appList.add(AppItem(appName, appInfo.packageName, icon, isLocked))
                }
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewApps)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppAdapter(this, appList)
    }
}
