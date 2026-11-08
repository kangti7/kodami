package com.example.kopidannyemil

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate

/**
 * Dua mode tampilan: Terang & Gelap.
 * Sebelum pengguna memilih, aplikasi mengikuti pengaturan sistem HP.
 */
object ThemeHelper {
    private const val PREF = "settings"
    private const val KEY_DARK = "dark_mode"

    private fun prefs(c: Context) = c.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    private fun saved(c: Context): Boolean? =
        if (prefs(c).contains(KEY_DARK)) prefs(c).getBoolean(KEY_DARK, false) else null

    /** Apakah tampilan yang sedang aktif adalah mode gelap. */
    fun isDark(c: Context): Boolean = saved(c)
        ?: ((c.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)

    fun apply(c: Context) {
        val dark = saved(c) ?: return
        val targetMode = if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(targetMode)
    }

    fun setDark(c: Context, dark: Boolean) {
        prefs(c).edit().putBoolean(KEY_DARK, dark).apply()
        val targetMode = if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(targetMode)
        (c as? Activity)?.recreate()
    }

    fun toggle(c: Context) = setDark(c, !isDark(c))
}

class KopiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeHelper.apply(this)
        OrderStore.init(this)
        AppData.loadAccount(this)
        installInsetsHandler()
    }
}

/** Ikon bulan/matahari di pojok kanan atas (bila layout punya ImageView @id/btnTheme). */
fun Activity.setupThemeToggle() {
    val btn = findViewById<ImageView>(R.id.btnTheme) ?: return
    btn.setImageResource(
        if (ThemeHelper.isDark(this)) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
    )
    btn.setOnClickListener { ThemeHelper.toggle(this) }
}
