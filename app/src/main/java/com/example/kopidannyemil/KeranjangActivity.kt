package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KeranjangActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var scroll: View
    private lateinit var emptyView: View
    private lateinit var summaryBar: View
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)
        showPendingNotif()

        container = findViewById(R.id.cartContainer)
        scroll = findViewById(R.id.scrollCart)
        emptyView = findViewById(R.id.emptyView)
        summaryBar = findViewById(R.id.summaryBar)
        tvTotal = findViewById(R.id.tvTotal)

        findViewById<Button>(R.id.btnBrowse).setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            if (AppData.cartCount() == 0) {
                showNotif("Keranjang masih kosong, pilih menu dulu ya", NotifType.ERROR)
            } else {
                startActivity(Intent(this, AlamatActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        render()
        setupBottomNav(1)
    }

    private fun render() {
        container.removeAllViews()
        val empty = AppData.cart.isEmpty()
        emptyView.visibility = if (empty) View.VISIBLE else View.GONE
        scroll.visibility = if (empty) View.GONE else View.VISIBLE
        summaryBar.visibility = if (empty) View.GONE else View.VISIBLE

        AppData.cart.keys.sorted().forEach { index ->
            val item = AppData.menu[index]
            val qty = AppData.cart[index] ?: 0

            val row = layoutInflater.inflate(R.layout.item_menu, container, false)
            bindMenuImage(row, item)
            row.findViewById<TextView>(R.id.tvEmoji).text = item.emoji
            row.findViewById<TextView>(R.id.tvName).text = item.name
            row.findViewById<TextView>(R.id.tvDesc).text = "${AppData.rupiah(item.price)} x $qty"
            row.findViewById<TextView>(R.id.tvPrice).text = AppData.rupiah(item.price * qty)
            row.findViewById<TextView>(R.id.tvQty).text = qty.toString()

            row.findViewById<TextView>(R.id.btnPlus).setOnClickListener {
                AppData.cart[index] = qty + 1
                render(); setupBottomNav(1)
            }
            row.findViewById<TextView>(R.id.btnMinus).setOnClickListener {
                if (qty <= 1) AppData.cart.remove(index) else AppData.cart[index] = qty - 1
                render(); setupBottomNav(1)
            }
            container.addView(row)
        }
        tvTotal.text = AppData.rupiah(AppData.cartTotal())
    }
}
