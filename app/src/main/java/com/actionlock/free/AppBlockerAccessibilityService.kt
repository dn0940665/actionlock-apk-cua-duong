package com.actionlock.free

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import org.json.JSONArray

/**
 * Monitors foreground app launches AND actively re-checks every 1 second so that
 * when an app's unlocked timer (15/30/60/120 mins) expires while the user is still
 * scrolling inside TikTok/Instagram/YouTube, it immediately kicks them out to the Exercise screen!
 */
class AppBlockerAccessibilityService : AccessibilityService() {

    private var currentForegroundPkg: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private val periodicExpiryChecker = object : Runnable {
        override fun run() {
            if (currentForegroundPkg.isNotEmpty() && currentForegroundPkg != packageName) {
                checkAndBlockIfNeeded(currentForegroundPkg)
            }
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        handler.post(thePeriodicRunner())
    }

    private fun thePeriodicRunner(): Runnable = thePeriodicExpiryChecker

    private val thePeriodicExpiryChecker: Runnable
        get() = periodicExpiryChecker

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg.startsWith("com.android.systemui") || pkg.contains("inputmethod")) return
        currentForegroundPkg = pkg
        if (pkg != packageName) {
            checkAndBlockIfNeeded(pkg)
        }
    }

    private fun checkAndBlockIfNeeded(pkg: String) {
        val prefs = getSharedPreferences("actionlock_prefs", Context.MODE_PRIVATE)
        val rawJson = prefs.getString("locked_apps_json", null) ?: return
        val now = System.currentTimeMillis()

        try {
            val arr = JSONArray(rawJson)
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                val targetPkg = item.optString("packageName")
                val isLocked = item.optBoolean("locked", false)
                val expiryMs = item.optLong("unlockExpiryMs", 0L)
                val appName = item.optString("appName", pkg)

                if (targetPkg == pkg && isLocked && now > expiryMs) {
                    val interceptIntent = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra("INTERCEPTED_PACKAGE", pkg)
                        putExtra("INTERCEPTED_LABEL", appName)
                    }
                    startActivity(interceptIntent)
                    break
                }
            }
        } catch (_: Exception) {}
    }

    override fun onInterrupt() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
