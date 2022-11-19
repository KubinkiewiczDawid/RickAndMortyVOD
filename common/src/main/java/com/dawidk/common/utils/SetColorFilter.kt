package com.dawidk.common.utils

import android.graphics.BlendMode.SRC_ATOP
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ProgressBar

fun setColorFilter(progressBar: ProgressBar) {
    if (VERSION.SDK_INT >= VERSION_CODES.Q) {
        progressBar.progressDrawable.colorFilter = BlendModeColorFilter(
            Color.RED, SRC_ATOP
        )
    } else {
        progressBar.progressDrawable.setColorFilter(
            Color.RED, PorterDuff.Mode.SRC_ATOP
        )
    }
}