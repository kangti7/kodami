package com.example.kopidannyemil

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Android 15+ menggambar aplikasi sampai ke bawah status bar & navigation bar (edge-to-edge).
 * Handler ini memberi jarak otomatis secara aman agar konten tidak tertabrak status bar / gesture bar / keyboard.
 */
fun Application.installInsetsHandler() {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityStarted(a: Activity) {
            val content = a.findViewById<View>(android.R.id.content) ?: return
            if (content.tag == "kn_insets") return
            content.tag = "kn_insets"

            ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

                val topPadding = if (statusBar.top in 1..v.context.dp(120)) statusBar.top else 0
                val bottomPadding = maxOf(navBar.bottom, ime.bottom)

                v.setPadding(0, topPadding, 0, bottomPadding)
                insets
            }
            ViewCompat.requestApplyInsets(content)
        }

        override fun onActivityCreated(a: Activity, s: Bundle?) {}
        override fun onActivityResumed(a: Activity) {}
        override fun onActivityPaused(a: Activity) {}
        override fun onActivityStopped(a: Activity) {}
        override fun onActivitySaveInstanceState(a: Activity, s: Bundle) {}
        override fun onActivityDestroyed(a: Activity) {}
    })
}
