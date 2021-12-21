package com.dawidk.common.errorHandling.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.Display
import android.view.View
import android.view.WindowManager
import kotlin.math.ceil
import kotlin.math.roundToInt

object BlurredBitmap {

    private const val BITMAP_SCALE = 0.4f
    private const val BLUR_RADIUS = 25f

    fun getBlur(context: Context?, image: Bitmap): Bitmap? {
        val scaledWidth = (image.width * BITMAP_SCALE).roundToInt().toInt()
        val scaledHeight = (image.height * BITMAP_SCALE).roundToInt().toInt()
        // create bitmaps, screenshot bitmap gets scaled for better effect and less computation
        val inputBitmap = Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        // create render script instance and its allocations from bitmaps
        val renderScript = RenderScript.create(context)
        val inAllocation = Allocation.createFromBitmap(renderScript, inputBitmap)
        val outAllocation = Allocation.createFromBitmap(renderScript, outputBitmap)
        // create blur script
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        // set bitmap and required blur radius
        blurScript.setRadius(BLUR_RADIUS)
        blurScript.setInput(inAllocation)
        blurScript.forEach(outAllocation)
        outAllocation.copyTo(outputBitmap)

        return outputBitmap
    }

    fun takeScreenShot(activity: Activity): Bitmap? {
        val view = activity.window.decorView.rootView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        val width: Int = getDeviceWidth(view)
        val height: Int = getDeviceHeight(view)
        // check if activity is displayed
        if (width == 0 || height == 0 || bitmap == null || height > bitmap.height || width > bitmap.width) {
            return null
        }
        val statusBarHeight: Int = getStatusBarHeight(activity)
        // cut status bar from bitmap to avoid jumping images
        val finalBitmap =
            Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return finalBitmap
    }

    private fun getDeviceWidth(view: View?): Int {
        val size = Point()
        getDisplay(view)?.getSize(size)
        return size.x
    }

    private fun getDeviceHeight(view: View?): Int {
        val size = Point()
        getDisplay(view)?.getSize(size)
        return size.y
    }

    private fun getDisplay(view: View?): Display? {
        val windowManager =
            view?.context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        return windowManager?.defaultDisplay
    }

    private fun getStatusBarHeight(context: Context): Int {
        return ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * context.resources.displayMetrics.density).toDouble())
            .toInt()
    }
}