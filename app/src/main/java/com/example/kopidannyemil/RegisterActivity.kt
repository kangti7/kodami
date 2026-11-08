package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupThemeToggle()

        val etNama = findViewById<EditText>(R.id.etNama)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        setupPasswordToggle(etPassword, findViewById(R.id.btnTogglePw))

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val nama = etNama.text.toString().trim()
            val user = etUsername.text.toString().trim()
            val pass = etPassword.text.toString()

            when {
                nama.isEmpty() || user.isEmpty() || pass.isEmpty() ->
                    showNotif("Semua kolom wajib diisi", NotifType.ERROR)
                pass.length < 4 ->
                    showNotif("Password minimal 4 karakter", NotifType.ERROR)
                else -> {
                    AppData.regName = nama
                    AppData.regUsername = user
                    AppData.regPassword = pass
                    AppData.saveAccount(this)
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                            .withNotif("Registrasi berhasil, silakan login")
                    )
                    finish()
                }
            }
        }
    }
}
