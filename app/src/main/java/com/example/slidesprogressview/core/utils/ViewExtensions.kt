package com.example.slidesprogressview.core.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.visibleIf(isVisible: Boolean) {
    return if (isVisible) {
        visible()
    } else {
        gone()
    }
}

fun View.onClick(click: (View) -> Unit) {
    setOnClickListener {
        click(it)
    }
}
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
