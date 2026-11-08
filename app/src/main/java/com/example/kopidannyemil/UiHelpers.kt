package com.example.kopidannyemil

import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Tampilkan foto menu bila file-nya ada di res/drawable (mis. menu_kopi_susu.jpg),
 * kalau belum ada otomatis memakai emoji sebagai pengganti.
 */
fun bindMenuImage(row: View, item: MenuItem) {
    val ctx = row.context
    val resId = if (item.image.isEmpty()) 0
    else ctx.resources.getIdentifier(item.image, "drawable", ctx.packageName)

    val img = row.findViewById<ImageView>(R.id.imgMenu)
    val emoji = row.findViewById<TextView>(R.id.tvEmoji)
    if (resId != 0) {
        img.setImageResource(resId)
        img.visibility = View.VISIBLE
        emoji.visibility = View.GONE
    } else {
        img.visibility = View.GONE
        emoji.visibility = View.VISIBLE
    }
}
