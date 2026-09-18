package com.mylock.app

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.SortedMap
import java.util.TreeMap

class MonitorService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var usageStatsManager: UsageStatsManager
    private var isRunning = false
    private var lastPackageName = ""

    private val checkRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val time = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 10,
                time
            )

            if (!stats.isNullOrEmpty()) {
                val mySortedMap: SortedMap<Long, android.app.usage.UsageStats> = TreeMap()
                for (usageStats in stats) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                
                if (mySortedMap.isNotEmpty()) {
                    val currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName

                    // Check if current app is locked and is not our own app
                    if (currentApp != packageName && isAppLocked(currentApp)) {
                        if (currentApp != lastPackageName) {
                            lastPackageName = currentApp
                            val intent = Intent(applicationContext, LockScreenActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra("locked_package", currentApp)
                            }
                            startActivity(intent)
                        }
                    } else {
                        lastPackageName = currentApp
                    }
                }
            }
            // Repeat check every 1 second
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        isRunning = true
        handler.post(checkRunnable)
    }

    private fun isAppLocked(pkgName: String): Boolean {
        val prefs = getSharedPreferences("MyLockPrefs", Context.MODE_PRIVATE)
        // You can save locked app states here. By default, let's lock Settings for testing:
        // package name for Android Settings is "com.android.settings"
        if (pkgName == "com.android.settings") return true
        
        return prefs.getBoolean("lock_$pkgName", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(checkRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
