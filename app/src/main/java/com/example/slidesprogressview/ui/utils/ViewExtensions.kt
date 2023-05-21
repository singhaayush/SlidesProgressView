package com.example.slidesprogressview.ui.utils

import android.content.Context
import android.view.View
import android.widget.Toast


fun View.onClick(click: (View) -> Unit) {
    setOnClickListener {
        click(it)
    }
}
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
