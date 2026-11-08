package com.example.kopidannyemil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

enum class NotifType { SUCCESS, ERROR, INFO }

const val EXTRA_NOTIF_MSG = "notif_msg"
const val EXTRA_NOTIF_TYPE = "notif_type"

fun Context.dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

/** Notifikasi banner di dalam aplikasi (pengganti Toast bawaan Android). */
fun Activity.showNotif(message: String, type: NotifType = NotifType.INFO, title: String? = null) {
    val content = findViewById<ViewGroup>(android.R.id.content) ?: return
    content.post {
        content.findViewWithTag<View>("kn_notif")?.let { content.removeView(it) }

        val banner = layoutInflater.inflate(R.layout.notif_banner, content, false)
        banner.tag = "kn_notif"

        val (emoji, color, defaultTitle) = when (type) {
            NotifType.SUCCESS -> Triple("✅", 0xFF2E9E5B.toInt(), "Berhasil")
            NotifType.ERROR -> Triple("⚠️", 0xFFD32F2F.toInt(), "Oops")
            NotifType.INFO -> Triple("🔔", 0xFF8E24AA.toInt(), "Info")
        }

        banner.findViewById<TextView>(R.id.notifIcon).apply {
            text = emoji
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor((color and 0x00FFFFFF) or 0x33000000)
            }
        }
        banner.findViewById<TextView>(R.id.notifTitle).text = title ?: defaultTitle
        banner.findViewById<TextView>(R.id.notifMsg).text = message

        // jarak status bar sudah ditangani Insets.kt
        val top = dp(12)

        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        ).apply { setMargins(dp(16), top, dp(16), 0) }
        content.addView(banner, lp)

        val hideOffset = -dp(180).toFloat()
        banner.translationY = hideOffset
        banner.alpha = 0f
        banner.animate().translationY(0f).alpha(1f).setDuration(280).start()

        val dismiss = {
            banner.animate().translationY(hideOffset).alpha(0f).setDuration(240)
                .withEndAction { (banner.parent as? ViewGroup)?.removeView(banner) }
                .start()
        }
        banner.setOnClickListener { dismiss() }
        banner.postDelayed({ if (banner.isAttachedToWindow) dismiss() }, 2800)
    }
}

/** Menempelkan pesan notifikasi ke Intent agar tampil di layar tujuan. */
fun Intent.withNotif(message: String, type: NotifType = NotifType.SUCCESS): Intent = apply {
    putExtra(EXTRA_NOTIF_MSG, message)
    putExtra(EXTRA_NOTIF_TYPE, type.name)
}

/** Dipanggil di onCreate layar tujuan untuk menampilkan notifikasi kiriman layar sebelumnya. */
fun Activity.showPendingNotif() {
    val msg = intent.getStringExtra(EXTRA_NOTIF_MSG) ?: return
    val type = runCatching { NotifType.valueOf(intent.getStringExtra(EXTRA_NOTIF_TYPE) ?: "") }
        .getOrDefault(NotifType.INFO)
    intent.removeExtra(EXTRA_NOTIF_MSG)
    intent.removeExtra(EXTRA_NOTIF_TYPE)
    showNotif(msg, type)
}
