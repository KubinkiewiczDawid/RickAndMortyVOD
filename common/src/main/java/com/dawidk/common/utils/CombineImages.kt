package com.dawidk.common.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import kotlin.math.sqrt

fun combineImages(bitmaps: List<Bitmap>): Bitmap {
    val rowsNumber: Int
    val colsNumber: Int

    if (sqrt(bitmaps.size.toDouble()) % 1 == 0.0) {
        rowsNumber = sqrt(bitmaps.size.toDouble()).toInt()
        colsNumber = sqrt(bitmaps.size.toDouble()).toInt()
    } else if (bitmaps.size == 2) {
        rowsNumber = 1
        colsNumber = 2
    } else if (bitmaps.size % 2 == 0) {
        rowsNumber = 2
        colsNumber = bitmaps.size / 2
    } else {
        rowsNumber = if (bitmaps.size == 3) 2 else 3
        colsNumber = if (bitmaps.size == 3) 2 else (bitmaps.size - 1) / 2
    }
    val result =
        Bitmap.createBitmap(
            bitmaps.first().width * colsNumber,
            bitmaps.first().height * rowsNumber,
            Bitmap.Config.ARGB_8888
        )
    val canvas = Canvas(result)

    for (i in bitmaps.indices) {
        if (bitmaps.size % 2 != 0 && i == bitmaps.indices.last) {
            canvas.drawBitmap(
                bitmaps[i],
                null,
                Rect(
                    0, (bitmaps[i].height * (i / colsNumber)),
                    bitmaps[i].width * colsNumber, bitmaps[i].height * rowsNumber * colsNumber
                ),
                null
            )

        } else {
            canvas.drawBitmap(
                bitmaps[i],
                (bitmaps[i].width * (i % colsNumber)).toFloat(),
                (bitmaps[i].height * (i / colsNumber)).toFloat(),
                null
            )
        }
    }
    return result
}