package com.example.kopidannyemil

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var tvCartSummary: TextView
    private lateinit var container: LinearLayout
    private lateinit var chips: List<TextView>
    private val qtyViews = mutableMapOf<Int, TextView>()

    // null = semua kategori
    private var filter: String? = null
    private var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupThemeToggle()
        showPendingNotif()

        findViewById<TextView>(R.id.tvGreeting).text = "Halo, ${AppData.displayName()}"
        tvCartSummary = findViewById(R.id.tvCartSummary)
        container = findViewById(R.id.menuContainer)

        // bar keranjang bisa diklik -> buka halaman Keranjang (tanpa harus memesan dulu)
        tvCartSummary.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        chips = listOf(
            findViewById(R.id.chipAll),
            findViewById(R.id.chipDrink),
            findViewById(R.id.chipFood)
        )
        chips[0].setOnClickListener { setFilter(null) }
        chips[1].setOnClickListener { setFilter("Minuman") }
        chips[2].setOnClickListener { setFilter("Makanan") }

        // pencarian menu
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                query = s?.toString()?.trim().orEmpty()
                renderMenu(animate = false)
            }
        })
        etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else false
        }

        setFilter(null)
    }

    override fun onResume() {
        super.onResume()
        // sinkronkan jumlah (keranjang bisa diubah dari halaman Keranjang)
        qtyViews.forEach { (index, tv) -> tv.text = (AppData.cart[index] ?: 0).toString() }
        updateSummary()
        setupBottomNav(0)
    }

    private fun setFilter(category: String?) {
        filter = category
        val selected = when (category) {
            "Minuman" -> 1
            "Makanan" -> 2
            else -> 0
        }
        chips.forEachIndexed { i, chip ->
            val on = i == selected
            chip.setBackgroundResource(if (on) R.drawable.bg_chip_on else R.drawable.bg_chip_off)
            chip.setTextColor(getColor(if (on) R.color.on_primary else R.color.text_primary))
        }
        renderMenu(animate = true)
    }

    private fun renderMenu(animate: Boolean) {
        container.removeAllViews()
        qtyViews.clear()
        val q = query.lowercase()
        var shown = 0

        AppData.menu.forEachIndexed { index, item ->
            if (filter != null && item.category != filter) return@forEachIndexed
            if (q.isNotEmpty() &&
                !(item.name.lowercase().contains(q) || item.desc.lowercase().contains(q))
            ) return@forEachIndexed

            val row = layoutInflater.inflate(R.layout.item_menu, container, false)
            bindMenuImage(row, item)
            row.findViewById<TextView>(R.id.tvEmoji).text = item.emoji
            row.findViewById<TextView>(R.id.tvName).text = item.name
            row.findViewById<TextView>(R.id.tvDesc).text = item.desc
            row.findViewById<TextView>(R.id.tvPrice).text = AppData.rupiah(item.price)

            val tvQty = row.findViewById<TextView>(R.id.tvQty)
            tvQty.text = (AppData.cart[index] ?: 0).toString()
            qtyViews[index] = tvQty

            row.findViewById<TextView>(R.id.btnPlus).setOnClickListener {
                val qty = (AppData.cart[index] ?: 0) + 1
                AppData.cart[index] = qty
                tvQty.text = qty.toString()
                updateSummary()
                setupBottomNav(0)
            }
            row.findViewById<TextView>(R.id.btnMinus).setOnClickListener {
                val qty = (AppData.cart[index] ?: 0) - 1
                if (qty <= 0) AppData.cart.remove(index) else AppData.cart[index] = qty
                tvQty.text = maxOf(qty, 0).toString()
                updateSummary()
                setupBottomNav(0)
            }

            if (animate) {
                // animasi muncul (fade + slide up)
                row.alpha = 0f
                row.translationY = 40f
                row.animate().alpha(1f).translationY(0f)
                    .setStartDelay(shown * 60L).setDuration(300).start()
            }
            shown++
            container.addView(row)
        }

        if (shown == 0) {
            container.addView(TextView(this).apply {
                text = "Menu tidak ditemukan 😢\nCoba kata kunci lain ya."
                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.gray_text))
                textSize = 14f
                setPadding(0, dp(48), 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }

    private fun updateSummary() {
        val count = AppData.cartCount()
        tvCartSummary.text = if (count == 0) {
            "🛒 Keranjang kosong • ketuk untuk membuka"
        } else {
            "🛒 $count item • ${AppData.rupiah(AppData.cartTotal())}  ›"
        }
    }
}
