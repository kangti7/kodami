package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlamatActivity : AppCompatActivity() {

    private val paymentViews = mutableListOf<Pair<TextView, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alamat)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etTelepon = findViewById<EditText>(R.id.etTelepon)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)

        etNama.setText(AppData.recipient.ifEmpty { AppData.name })
        etTelepon.setText(AppData.phone)
        etAlamat.setText(AppData.address)

        findViewById<TextView>(R.id.tvTotalBayar).text = AppData.rupiah(AppData.cartTotal())
        buildPaymentOptions()

        findViewById<Button>(R.id.btnOrder).setOnClickListener {
            val nama = etNama.text.toString().trim()
            val telp = etTelepon.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val payment = AppData.selectedPayment

            if (AppData.cartCount() == 0) {
                showNotif("Keranjang kosong, tidak ada yang dipesan", NotifType.ERROR)
                return@setOnClickListener
            }
            if (nama.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
                showNotif("Lengkapi semua data pengiriman", NotifType.ERROR)
                return@setOnClickListener
            }
            if (payment == null) {
                showNotif("Pilih metode pembayaran dulu", NotifType.ERROR)
                return@setOnClickListener
            }
            AppData.recipient = nama
            AppData.phone = telp
            AppData.address = alamat

            // buat pesanan & simpan ke riwayat
            val lines = AppData.cart.entries.sortedBy { it.key }.map { (i, qty) ->
                val m = AppData.menu[i]
                OrderLine(m.name, qty, m.price, m.emoji)
            }
            val order = Order(
                id = OrderStore.nextId(),
                createdAt = System.currentTimeMillis(),
                items = lines,
                total = AppData.cartTotal(),
                recipient = nama,
                phone = telp,
                address = alamat,
                payment = payment
            )
            OrderStore.add(order)
            AppData.clearCart()
            AppData.selectedPayment = null

            startActivity(
                Intent(this, KonfirmasiActivity::class.java)
                    .putExtra(KonfirmasiActivity.EXTRA_ORDER_ID, order.id)
                    .withNotif("Pesanan ${OrderStore.code(order)} berhasil dibuat")
            )
            finish()
        }
    }

    /** Pilihan metode pembayaran (dummy): ketuk salah satu untuk memilih. */
    private fun buildPaymentOptions() {
        val container = findViewById<LinearLayout>(R.id.paymentContainer)
        AppData.paymentMethods.forEach { method ->
            val tv = TextView(this).apply {
                text = method.name
                textSize = 14f
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(14), dp(18), dp(14))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(10) }
                setOnClickListener {
                    AppData.selectedPayment = method.name
                    refreshPaymentStyles()
                }
            }
            paymentViews.add(tv to method.name)
            container.addView(tv)
        }
        refreshPaymentStyles()
    }

    private fun refreshPaymentStyles() {
        paymentViews.forEach { (tv, name) ->
            val on = name == AppData.selectedPayment
            tv.setBackgroundResource(if (on) R.drawable.bg_chip_on else R.drawable.bg_chip_off)
            tv.setTextColor(getColor(if (on) R.color.on_primary else R.color.text_primary))
            tv.text = if (on) "✓  $name" else name
        }
    }
}
