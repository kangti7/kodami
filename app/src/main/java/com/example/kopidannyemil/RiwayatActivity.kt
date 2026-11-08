package com.example.kopidannyemil

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RiwayatActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var scroll: View
    private lateinit var emptyView: View

    private val handler = Handler(Looper.getMainLooper())
    private var lastStatus: Map<Int, Int> = emptyMap()

    private val stageColors = intArrayOf(0xFF607D8B.toInt(), 0xFFF59E0B.toInt(), 0xFF3B82F6.toInt(), 0xFF2E9E5B.toInt())
    private val green = 0xFF2E9E5B.toInt()

    private val ticker = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        showPendingNotif()
        container = findViewById(R.id.historyContainer)
        scroll = findViewById(R.id.scrollHistory)
        emptyView = findViewById(R.id.emptyView)
    }

    override fun onResume() {
        super.onResume()
        render()
        lastStatus = currentStatuses()
        setupBottomNav(2)
        handler.postDelayed(ticker, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    private fun currentStatuses(): Map<Int, Int> =
        OrderStore.all().associate { it.id to OrderStore.status(it) }

    /** Cek tiap detik: kalau status berubah, tampilkan notifikasi & perbarui tampilan. */
    private fun tick() {
        val now = currentStatuses()
        var changed = false
        for ((id, st) in now) {
            val old = lastStatus[id]
            if (old != null && old != st) {
                changed = true
                val order = OrderStore.get(id) ?: continue
                showNotif(
                    "${OrderStore.code(order)} • ${OrderStore.STAGE_TITLES[st]}",
                    if (st == 3) NotifType.SUCCESS else NotifType.INFO,
                    "Status pesanan"
                )
            }
        }
        if (changed) render()
        lastStatus = now
    }

    private fun render() {
        container.removeAllViews()
        val orders = OrderStore.all()
        emptyView.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        scroll.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE

        orders.forEach { order ->
            val st = OrderStore.status(order)
            val card = layoutInflater.inflate(R.layout.item_order, container, false)

            card.findViewById<TextView>(R.id.tvOrderId).text = OrderStore.code(order)
            card.findViewById<TextView>(R.id.tvDate).text = OrderStore.dateTime(order.createdAt)
            card.findViewById<TextView>(R.id.tvItems).text =
                order.items.joinToString("\n") { "${it.emoji} ${it.qty}x ${it.name}" }
            card.findViewById<TextView>(R.id.tvTotal).text = AppData.rupiah(order.total)
            card.findViewById<TextView>(R.id.tvPayment).text = order.payment

            card.findViewById<TextView>(R.id.tvStatus).apply {
                text = "${OrderStore.STAGE_EMOJI[st]} ${OrderStore.STAGE_TITLES[st]}"
                background = GradientDrawable().apply {
                    cornerRadius = dp(14).toFloat()
                    setColor(stageColors[st])
                }
            }

            card.findViewById<TextView>(R.id.tvEta).text =
                if (st == 3) "✅ Diterima pukul ${OrderStore.clock(OrderStore.stageTime(order, 3))}"
                else "🕒 Estimasi tiba: ${OrderStore.clock(OrderStore.stageTime(order, 3))}"

            val steps = card.findViewById<LinearLayout>(R.id.stepsContainer)
            for (stage in 0..3) {
                steps.addView(buildStep(steps, order, stage, st))
            }
            card.findViewById<View>(R.id.btnReorder).setOnClickListener { reorder(order) }
            container.addView(card)
        }
    }

    /** Masukkan lagi semua item pesanan ini ke keranjang. */
    private fun reorder(order: Order) {
        var added = 0
        order.items.forEach { line ->
            val idx = AppData.menu.indexOfFirst { it.name == line.name }
            if (idx >= 0) {
                AppData.cart[idx] = (AppData.cart[idx] ?: 0) + line.qty
                added += line.qty
            }
        }
        if (added == 0) {
            showNotif("Menu pada pesanan ini sudah tidak tersedia", NotifType.ERROR)
            return
        }
        startActivity(
            Intent(this, KeranjangActivity::class.java)
                .withNotif("$added item dari ${OrderStore.code(order)} masuk ke keranjang")
        )
    }

    private fun buildStep(parent: LinearLayout, order: Order, stage: Int, st: Int): View {
        val row = layoutInflater.inflate(R.layout.item_step, parent, false)
        val dot = row.findViewById<TextView>(R.id.stepDot)
        val line = row.findViewById<View>(R.id.stepLine)
        val title = row.findViewById<TextView>(R.id.stepTitle)
        val time = row.findViewById<TextView>(R.id.stepTime)

        val done = stage < st || st == 3
        val current = stage == st && st != 3
        val gray = ContextCompat.getColor(this, R.color.gray_text)

        dot.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            when {
                done -> setColor(green)
                current -> setColor(stageColors[stage])
                else -> {
                    setColor(Color.TRANSPARENT)
                    setStroke(dp(2), gray)
                }
            }
        }
        dot.text = when {
            done -> "✓"
            current -> OrderStore.STAGE_EMOJI[stage]
            else -> ""
        }

        title.text = OrderStore.STAGE_TITLES[stage]
        title.setTextColor(
            ContextCompat.getColor(this, if (done || current) R.color.text_primary else R.color.gray_text)
        )
        if (current) title.setTypeface(null, android.graphics.Typeface.BOLD)

        time.text = when {
            done -> OrderStore.clock(OrderStore.stageTime(order, stage))
            current -> "${OrderStore.STAGE_DESC[stage]} • ${OrderStore.clock(OrderStore.stageTime(order, stage))}"
            stage == 3 -> "Estimasi tiba ${OrderStore.clock(OrderStore.stageTime(order, 3))}"
            else -> "Menunggu"
        }

        if (stage == 3) {
            line.visibility = View.GONE
        } else {
            line.setBackgroundColor(if (stage < st) green else gray)
        }
        return row
    }
}
