package com.example.kopidannyemil

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderLine(val name: String, val qty: Int, val price: Int, val emoji: String)

data class Order(
    val id: Int,
    val createdAt: Long,
    val items: List<OrderLine>,
    val total: Int,
    val recipient: String,
    val phone: String,
    val address: String,
    val payment: String
)

/**
 * Riwayat pesanan (disimpan di SharedPreferences, bukan database).
 * Status pesanan dihitung dari waktu berjalan sebagai SIMULASI:
 * 0 Pesanan dibuat -> 1 Sedang disiapkan -> 2 Sedang dikirim -> 3 Pesanan diterima.
 */
object OrderStore {
    /** Detik sejak pesanan dibuat untuk masuk ke tiap tahap (ubah jika ingin lebih lambat/cepat). */
    val STAGE_AT_SECONDS = longArrayOf(0, 15, 40, 70)

    val STAGE_TITLES = listOf("Pesanan dibuat", "Sedang disiapkan", "Sedang dikirim", "Pesanan diterima")
    val STAGE_EMOJI = listOf("📝", "👨‍🍳", "🛵", "✅")
    val STAGE_DESC = listOf(
        "Pesananmu sudah kami terima",
        "Barista kami sedang menyiapkan pesananmu",
        "Kurir sedang menuju alamatmu",
        "Selamat menikmati!"
    )

    private lateinit var prefs: SharedPreferences
    private val orders = mutableListOf<Order>()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("orders", Context.MODE_PRIVATE)
        load()
    }

    fun all(): List<Order> = orders.sortedByDescending { it.createdAt }

    fun get(id: Int): Order? = orders.firstOrNull { it.id == id }

    fun nextId(): Int = (orders.maxOfOrNull { it.id } ?: 0) + 1

    fun add(order: Order) {
        orders.add(order)
        save()
    }

    fun code(order: Order): String = "#KN-" + String.format(Locale.US, "%04d", order.id)

    fun status(order: Order, now: Long = System.currentTimeMillis()): Int {
        val elapsed = (now - order.createdAt) / 1000
        var s = 0
        STAGE_AT_SECONDS.forEachIndexed { i, t -> if (elapsed >= t) s = i }
        return s
    }

    fun stageTime(order: Order, stage: Int): Long = order.createdAt + STAGE_AT_SECONDS[stage] * 1000

    fun dateTime(ms: Long): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(ms))

    fun clock(ms: Long): String =
        SimpleDateFormat("HH:mm:ss", Locale("id", "ID")).format(Date(ms))

    private fun save() {
        val arr = JSONArray()
        orders.forEach { o ->
            val items = JSONArray()
            o.items.forEach { l ->
                items.put(
                    JSONObject().put("name", l.name).put("qty", l.qty)
                        .put("price", l.price).put("emoji", l.emoji)
                )
            }
            arr.put(
                JSONObject().put("id", o.id).put("createdAt", o.createdAt).put("total", o.total)
                    .put("recipient", o.recipient).put("phone", o.phone)
                    .put("address", o.address).put("payment", o.payment).put("items", items)
            )
        }
        prefs.edit().putString("data", arr.toString()).apply()
    }

    private fun load() {
        orders.clear()
        try {
            val arr = JSONArray(prefs.getString("data", "[]"))
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val itemsArr = o.getJSONArray("items")
                val lines = (0 until itemsArr.length()).map {
                    val l = itemsArr.getJSONObject(it)
                    OrderLine(l.getString("name"), l.getInt("qty"), l.getInt("price"), l.getString("emoji"))
                }
                orders.add(
                    Order(
                        o.getInt("id"), o.getLong("createdAt"), lines, o.getInt("total"),
                        o.getString("recipient"), o.getString("phone"), o.getString("address"),
                        o.optString("payment", "-")
                    )
                )
            }
        } catch (e: Exception) {
            orders.clear()
        }
    }
}
