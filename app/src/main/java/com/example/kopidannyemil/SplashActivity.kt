package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupThemeToggle()
        showPendingNotif()

        // animasi logo (scale + fade) dan teks (fade)
        val logo = findViewById<View>(R.id.logo)
        logo.scaleX = 0.6f
        logo.scaleY = 0.6f
        logo.alpha = 0f
        logo.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(700).start()

        listOf(R.id.tvTitle, R.id.tvTagline).forEachIndexed { i, id ->
            val v = findViewById<View>(id)
            v.alpha = 0f
            v.animate().alpha(1f).setStartDelay(400L + i * 200L).setDuration(600).start()
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
