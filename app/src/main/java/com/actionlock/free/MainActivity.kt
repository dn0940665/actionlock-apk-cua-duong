package com.actionlock.free

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }

        // Start 24/7 KeepAliveForegroundService
        try {
            val svcIntent = Intent(this, KeepAliveForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(svcIntent)
            } else {
                startService(svcIntent)
            }
        } catch (_: Exception) {}

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.allowFileAccess = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                handleInterceptIntent(intent)
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread { request.grant(request.resources) }
            }
        }

        webView.addJavascriptInterface(AndroidNativeBridge(), "AndroidNative")
        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleInterceptIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        webView.evaluateJavascript("if(window.renderAll) window.renderAll();", null)
    }

    private fun handleInterceptIntent(intent: Intent?) {
        val pkg = intent?.getStringExtra("INTERCEPTED_PACKAGE") ?: return
        val label = intent.getStringExtra("INTERCEPTED_LABEL") ?: pkg
        intent.removeExtra("INTERCEPTED_PACKAGE")
        webView.evaluateJavascript(
            "if(window.onNativeAppIntercepted) window.onNativeAppIntercepted('$pkg', '$label');",
            null
        )
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expected = "$packageName/${AppBlockerAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expected) || enabledServices.contains(packageName)
    }

    inner class AndroidNativeBridge {

        @JavascriptInterface
        fun getSystemStatusJson(): String {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            val batteryIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pm.isIgnoringBatteryOptimizations(packageName)
            } else true
            val overlayEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(this@MainActivity)
            } else true

            val obj = JSONObject()
            obj.put("accessibilityEnabled", isAccessibilityServiceEnabled())
            obj.put("overlayEnabled", overlayEnabled)
            obj.put("batteryIgnored", batteryIgnored)
            return obj.toString()
        }

        @JavascriptInterface
        fun openAccessibilitySettings() {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        @JavascriptInterface
        fun openOverlaySettings() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        @JavascriptInterface
        fun requestIgnoreBatteryOptimizations() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        @JavascriptInterface
        fun getInstalledAppsJson(): String {
            val pm = packageManager
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val arr = JSONArray()
            for (app in apps) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && app.packageName != packageName) {
                    val item = JSONObject()
                    item.put("packageName", app.packageName)
                    item.put("name", pm.getApplicationLabel(app).toString())
                    arr.put(item)
                }
            }
            return arr.toString()
        }

        @JavascriptInterface
        fun syncLockedApps(jsonPayload: String) {
            val prefs = getSharedPreferences("actionlock_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("locked_apps_json", jsonPayload).apply()
        }
    }
}
