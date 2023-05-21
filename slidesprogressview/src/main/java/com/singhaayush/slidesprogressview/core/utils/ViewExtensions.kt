package com.singhaayush.slidesprogressview.core.utils

import android.view.View

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