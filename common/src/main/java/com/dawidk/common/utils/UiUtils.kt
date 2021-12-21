package com.dawidk.common.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.common.R
import kotlin.math.roundToInt

fun Context.isTablet(): Boolean = resources.getBoolean(R.bool.isTablet)

fun selectLayoutManager(context: Context): RecyclerView.LayoutManager = if (context.isTablet()) {
    GridLayoutManager(context, context.resources.getInteger(R.integer.grid_layout_span_count))
} else {
    LinearLayoutManager(context)
}

fun TextView.setAutoSize(
    autoSizeMinTextSize: Int = resources.getInteger(R.integer.def_auto_size_min_text_size),
    autoSizeMaxTextSize: Int = resources.getInteger(R.integer.def_auto_size_max_text_size),
    autoSizeStepGranularity: Int = resources.getInteger(R.integer.def_auto_size_step_granularity),
    maxLines: Int? = null
) {
    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
        this,
        autoSizeMinTextSize,
        autoSizeMaxTextSize,
        autoSizeStepGranularity,
        TypedValue.COMPLEX_UNIT_SP
    )
    maxLines?.let {
        this.maxLines = it
    }
}

fun View.setPercentageSize(
    parent: ViewGroup,
    percentageHeight: Float? = null,
    percentageWidth: Float? = null
) {
    parent.context?.let {
        val newLayoutParams = this.layoutParams
        percentageHeight?.let {
            newLayoutParams.height = (parent.height * it).roundToInt()
        }
        percentageWidth?.let {
            if (it.toInt() == 1) {
                newLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                newLayoutParams.width = (parent.width * it).roundToInt()
            }
        }
        this.layoutParams = newLayoutParams
    }
}

fun Float.toPx(resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    ).toInt()
}

fun Int.toPx(resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()
}

fun View.setMargins(start: Int = 0, end: Int = 0, top: Int = 0, bottom: Int = 0) {
    (this.layoutParams as ViewGroup.MarginLayoutParams).apply {
        marginStart = start
        marginEnd = end
        topMargin = top
        bottomMargin = bottom
    }
    this.requestLayout()
}

fun EditText.hideKeyboardOnAction(context: Context, action: Int) {
    this.setOnEditorActionListener { v, actionId, _ ->
        if (actionId == action) {
            v.clearFocus()
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
            true
        } else false
    }
}