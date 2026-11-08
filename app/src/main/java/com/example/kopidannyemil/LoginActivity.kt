package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupThemeToggle()
        showPendingNotif()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        setupPasswordToggle(etPassword, findViewById(R.id.btnTogglePw))

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val user = etUsername.text.toString().trim()
            val pass = etPassword.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                showNotif("Username dan password wajib diisi", NotifType.ERROR)
                return@setOnClickListener
            }

            val cocokAkun = AppData.hasAccount() &&
                    user == AppData.regUsername && pass == AppData.regPassword
            val cocokDemo = user == "admin" && pass == "admin"

            if (cocokAkun || cocokDemo) {
                if (cocokAkun) {
                    AppData.name = AppData.regName
                    AppData.username = AppData.regUsername
                } else {
                    AppData.name = "Admin"
                    AppData.username = "admin"
                }
                startActivity(
                    Intent(this, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .withNotif("Selamat datang, ${AppData.displayName()}! ☕")
                )
            } else {
                showNotif("Username atau password salah", NotifType.ERROR)
            }
        }
    }
}
