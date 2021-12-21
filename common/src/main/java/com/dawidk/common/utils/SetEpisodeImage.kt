package com.dawidk.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import com.dawidk.common.R
import com.dawidk.common.constants.IMAGE_FULL_SIZE
import com.dawidk.core.domain.model.Character
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

suspend fun setEpisodeImage(
    imageView: ImageView,
    context: Context,
    characters: List<Character>,
    cacheKey: String,
    imagesNumber: Int,
    imageSize: Int = IMAGE_FULL_SIZE
) = coroutineScope {
    val imageLoader = context.imageLoader
    val memoryCacheKey = MemoryCache.Key(cacheKey)
    if (imageLoader.memoryCache[memoryCacheKey] == null) {
        val images = getRandomImages(characters, imagesNumber)
        val bitmaps = images.map {
            var image: Bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.placeholder_img
            )
            val job = launch {
                fetchBitmapImage(context, it)?.let { image = it }
            }

            if (!job.isActive) {
                return@coroutineScope
            }

            job.join()
            image
        }
        if (!isActive) return@coroutineScope
        val image = combineImages(bitmaps)
        imageLoader.memoryCache[memoryCacheKey] = image
    }
    val cachedImage = imageLoader.memoryCache[memoryCacheKey]
    val resizedImage = cachedImage?.let {
        Bitmap.createScaledBitmap(
            it,
            imageSize,
            imageSize,
            true
        )
    }
    imageView.load(resizedImage)
}