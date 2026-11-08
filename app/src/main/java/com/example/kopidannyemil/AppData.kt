package com.example.kopidannyemil

import android.content.Context
import java.util.Locale

data class MenuItem(
    val name: String,
    val desc: String,
    val price: Int,
    val emoji: String,
    val category: String,
    val image: String = ""   // nama file foto di res/drawable (tanpa ekstensi); kosong/tidak ada -> pakai emoji
)

data class PaymentMethod(val name: String, val note: String)

/** Penyimpanan data sederhana di memori (tanpa database, sesuai ketentuan tugas). */
object AppData {
    // Sesi login (akun yang sedang dipakai)
    var name: String = ""
    var username: String = ""

    // Akun terdaftar -> disimpan permanen di SharedPreferences
    var regName: String = ""
    var regUsername: String = ""
    var regPassword: String = ""

    fun hasAccount(): Boolean = regUsername.isNotEmpty()

    fun loadAccount(context: Context) {
        val p = context.getSharedPreferences("account", Context.MODE_PRIVATE)
        regName = p.getString("name", "") ?: ""
        regUsername = p.getString("username", "") ?: ""
        regPassword = p.getString("password", "") ?: ""
    }

    fun saveAccount(context: Context) {
        context.getSharedPreferences("account", Context.MODE_PRIVATE).edit()
            .putString("name", regName)
            .putString("username", regUsername)
            .putString("password", regPassword)
            .apply()
    }

    // Data pengiriman
    var recipient: String = ""
    var phone: String = ""
    var address: String = ""

    // Dummy data menu (min. 5 item)
    val menu = listOf(
        MenuItem("Kopi Susu Gula Aren", "Espresso, susu segar, gula aren", 18000, "☕", "Minuman", "menu_kopi_susu"),
        MenuItem("Cappuccino", "Espresso dengan foam susu lembut", 22000, "🥛", "Minuman", "menu_cappuccino"),
        MenuItem("Matcha Latte", "Matcha premium dengan susu", 24000, "🍵", "Minuman", "menu_matcha_latte"),
        MenuItem("Choco Hazelnut", "Coklat pekat dengan sirup hazelnut", 23000, "🍫", "Minuman", "menu_choco_hazelnut"),
        MenuItem("Roti Bakar Coklat Keju", "Roti bakar tebal, coklat & keju", 16000, "🍞", "Makanan", "menu_roti_bakar"),
        MenuItem("Kentang Goreng", "Kentang crispy dengan saus sambal", 15000, "🍟", "Makanan", "menu_kentang_goreng"),
        MenuItem("Pisang Goreng Madu", "Pisang goreng hangat dengan madu", 14000, "🍌", "Makanan", "menu_pisang_goreng"),
        MenuItem("Donat Kampung", "Donat empuk dengan gula halus", 12000, "🍩", "Makanan", "menu_donat")
    )

    // Metode pembayaran (dummy)
    val paymentMethods = listOf(
        PaymentMethod("💵 Bayar di Tempat (COD)", "Siapkan uang pas saat kurir tiba."),
        PaymentMethod("🏦 Transfer Bank", "Transfer ke rekening BCA 1234567890 a.n. Kopi dan Nyemil (dummy)."),
        PaymentMethod("🟢 GoPay", "Selesaikan pembayaran lewat aplikasi GoPay (simulasi)."),
        PaymentMethod("🟣 OVO", "Selesaikan pembayaran lewat aplikasi OVO (simulasi)."),
        PaymentMethod("🔵 DANA", "Selesaikan pembayaran lewat aplikasi DANA (simulasi)."),
        PaymentMethod("🔳 QRIS", "Scan kode QRIS dari aplikasi pembayaranmu (simulasi).")
    )
    var selectedPayment: String? = null

    fun paymentNote(name: String): String = paymentMethods.firstOrNull { it.name == name }?.note ?: ""

    // Keranjang: index menu -> jumlah
    val cart = mutableMapOf<Int, Int>()

    fun displayName(): String = name.ifBlank { "Teman Ngopi" }

    fun cartCount(): Int = cart.values.sum()

    fun cartTotal(): Int = cart.entries.sumOf { (i, q) -> menu[i].price * q }

    fun clearCart() = cart.clear()

    fun rupiah(n: Int): String = "Rp " + String.format(Locale("id", "ID"), "%,d", n)
}
