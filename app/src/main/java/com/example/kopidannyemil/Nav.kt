package com.example.kopidannyemil

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.widget.TextView

/** Bottom navigation: 0 = Home, 1 = Keranjang, 2 = Riwayat, 3 = Profile */
fun Activity.setupBottomNav(selected: Int) {
    val tabs = listOf<TextView>(
        findViewById(R.id.navHome),
        findViewById(R.id.navCart),
        findViewById(R.id.navHistory),
        findViewById(R.id.navProfile)
    )
    val targets = listOf(
        HomeActivity::class.java,
        KeranjangActivity::class.java,
        RiwayatActivity::class.java,
        ProfileActivity::class.java
    )

    val count = AppData.cartCount()
    tabs[1].text = if (count > 0) "🛒 $count\nKeranjang" else "🛒\nKeranjang"

    tabs.forEachIndexed { index, tv ->
        val active = index == selected
        tv.setBackgroundResource(if (active) R.drawable.bg_card else 0)
        tv.setTextColor(getColor(if (active) R.color.text_primary else R.color.gray_text))
        tv.setTypeface(null, if (active) Typeface.BOLD else Typeface.NORMAL)

        tv.setOnClickListener {
            if (index != selected) {
                val intent = Intent(this, targets[index])
                intent.addFlags(
                    if (index == 0) Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    else Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                )
                startActivity(intent)
            }
        }
    }
}
