package com.dawidk.common.utils

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.checkCallbackImplemented() {
    if (findCallback(T::class.java) == null) {
        throw noCallbackImplementedException<T>()
    }
}

fun <T> Fragment.findCallback(type: Class<T>): T? {
    return safeCast(parentFragment, type)
        ?: parentFragment?.findCallback(type)
        ?: safeCast(activity, type)
}

inline fun <reified T> Fragment.callback() =
    findCallback(T::class.java) ?: throw  noCallbackImplementedException<T>()

inline fun <reified T> noCallbackImplementedException() =
    IllegalStateException("Activity or parent Fragment must implement ${T::class.java.name}")

fun <T> safeCast(obj: Any?, type: Class<T>): T? {
    return if (type.isInstance(obj)) {
        obj as T
    } else {
        null
    }
}