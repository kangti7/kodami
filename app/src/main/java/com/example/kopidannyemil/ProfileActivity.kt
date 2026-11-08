package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupThemeToggle()

        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val etNama = findViewById<EditText>(R.id.etNama)

        tvGreeting.text = "Halo, ${AppData.displayName()}"
        tvUsername.text = if (AppData.username.isNotEmpty()) "@${AppData.username}" else ""
        etNama.setText(AppData.name)

        // pilihan tema: Terang / Gelap
        val chipLight = findViewById<TextView>(R.id.chipLight)
        val chipDark = findViewById<TextView>(R.id.chipDark)
        val dark = ThemeHelper.isDark(this)
        styleChip(chipLight, !dark)
        styleChip(chipDark, dark)
        chipLight.setOnClickListener { if (dark) ThemeHelper.setDark(this, false) }
        chipDark.setOnClickListener { if (!dark) ThemeHelper.setDark(this, true) }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val baru = etNama.text.toString().trim()
            if (baru.isEmpty()) {
                showNotif("Nama tidak boleh kosong", NotifType.ERROR)
            } else {
                AppData.name = baru
                if (AppData.hasAccount() && AppData.username == AppData.regUsername) {
                    AppData.regName = baru
                    AppData.saveAccount(this)
                }
                tvGreeting.text = "Halo, $baru"
                showNotif("Profil berhasil disimpan", NotifType.SUCCESS)
            }
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener { confirmLogout() }
    }

    override fun onResume() {
        super.onResume()
        setupBottomNav(3)
    }

    /** Dialog konfirmasi sebelum logout. */
    private fun confirmLogout() {
        val dialog: AlertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Logout?")
            .setMessage("Kamu yakin ingin keluar dari akun ini? Isi keranjang akan dikosongkan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Ya, Logout") { _, _ -> doLogout() }
            .create()
        dialog.show()
        val accent = getColor(R.color.text_primary)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(accent)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(accent)
    }

    private fun doLogout() {
        AppData.clearCart()
        AppData.name = ""
        AppData.username = ""
        startActivity(
            Intent(this, SplashActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .withNotif("Kamu sudah logout. Sampai jumpa! 👋", NotifType.INFO)
        )
    }

    private fun styleChip(tv: TextView, on: Boolean) {
        tv.setBackgroundResource(if (on) R.drawable.bg_chip_on else R.drawable.bg_chip_off)
        tv.setTextColor(getColor(if (on) R.color.on_primary else R.color.text_primary))
    }
}
