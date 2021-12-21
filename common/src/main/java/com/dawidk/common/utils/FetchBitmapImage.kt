package com.dawidk.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dawidk.common.constants.IMAGE_FULL_SIZE

suspend fun fetchBitmapImage(
    context: Context,
    url: String,
    imageSize: Int = IMAGE_FULL_SIZE
): Bitmap? {
    return try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .size(imageSize)
            .build()
        val result = (context.imageLoader.execute(request) as? SuccessResult)?.drawable
        (result as BitmapDrawable).bitmap
    } catch (e: Exception) {
        null
    }
}