package com.example.kopidannyemil

import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView

/** Ikon mata untuk menampilkan / menyembunyikan password. */
fun setupPasswordToggle(editText: EditText, toggle: ImageView) {
    var visible = false
    toggle.setOnClickListener {
        visible = !visible
        editText.transformationMethod =
            if (visible) null else PasswordTransformationMethod.getInstance()
        editText.setSelection(editText.text.length)
        toggle.setImageResource(if (visible) R.drawable.ic_visibility else R.drawable.ic_visibility_off)
    }
}
