package com.dawidk.common.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView

private const val COLOR_STRING_LENGTH = 6
private const val MAX_GRADIENT_LAYERS = 5

fun setLocationImage(
    imageView: ImageView,
    locationId: String,
    locationName: String
) {
    val colors =
        (locationId + locationName).toByteArray(Charsets.UTF_8).toHex().chunked(COLOR_STRING_LENGTH)
            .map { colors ->
                val nOfMissing = COLOR_STRING_LENGTH - colors.length
                var newString = ""
                if ((nOfMissing) > 0) {
                    repeat(nOfMissing) {
                        newString += "0"
                    }
                }
                Color.parseColor("#$colors$newString")
            }.toIntArray()
    val gd = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM, colors.take(MAX_GRADIENT_LAYERS).toIntArray()
    )
    gd.shape = GradientDrawable.OVAL
    imageView.setImageDrawable(gd)
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }