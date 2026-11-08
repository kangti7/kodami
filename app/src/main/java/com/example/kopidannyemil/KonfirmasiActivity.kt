package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KonfirmasiActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi)
        showPendingNotif()

        findViewById<TextView>(R.id.tvGreeting).text = "Halo, ${AppData.displayName()}"

        val order = OrderStore.get(intent.getIntExtra(EXTRA_ORDER_ID, -1))
        val tvDetail = findViewById<TextView>(R.id.tvDetail)
        if (order != null) {
            val sb = StringBuilder()
            sb.append("Pesanan ${OrderStore.code(order)}\n")
            order.items.forEach { sb.append("• ${it.name} x${it.qty}  (${AppData.rupiah(it.price * it.qty)})\n") }
            sb.append("\nTotal: ${AppData.rupiah(order.total)}\n")
            sb.append("Pembayaran: ${order.payment}\n")
            val note = AppData.paymentNote(order.payment)
            if (note.isNotEmpty()) sb.append("$note\n")
            sb.append("Estimasi tiba: ${OrderStore.clock(OrderStore.stageTime(order, 3))}\n\n")
            sb.append("Dikirim ke:\n${order.recipient} (${order.phone})\n${order.address}")
            tvDetail.text = sb.toString()
        }

        findViewById<Button>(R.id.btnTrack).setOnClickListener {
            startActivity(
                Intent(this, RiwayatActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
        findViewById<Button>(R.id.btnOk).setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}
