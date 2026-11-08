package com.example.kopidannyemil

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Process
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Layar diagnosis: kalau aplikasi crash, error ditampilkan di sini
 * sehingga bisa di-screenshot tanpa membuka Logcat.
 */
class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trace = intent.getStringExtra("trace") ?: "(tidak ada detail error)"
        val pad = (16 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(pad, pad * 4, pad, pad)
        }
        root.addView(TextView(this).apply {
            text = "Aplikasi berhenti karena error"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFFB00020.toInt())
        })
        root.addView(TextView(this).apply {
            text = "Screenshot layar ini lalu kirim ke Claude."
            setTextColor(Color.DKGRAY)
            setPadding(0, pad / 2, 0, pad)
        })

        val tv = TextView(this).apply {
            text = trace
            textSize = 11f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.BLACK)
            setTextIsSelectable(true)
        }
        root.addView(
            ScrollView(this).apply { addView(tv) },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        )

        root.addView(Button(this).apply {
            text = "Salin error"
            setOnClickListener {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("error", trace))
            }
        })
        root.addView(Button(this).apply {
            text = "Tutup"
            setOnClickListener {
                finishAffinity()
                Process.killProcess(Process.myPid())
            }
        })
        setContentView(root)
    }
}
