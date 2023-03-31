package com.dawidk.common.binding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val factory: (View) -> T
) : ReadOnlyProperty<Fragment, T>, Observer<LifecycleOwner> {

    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        fragment.viewLifecycleOwnerLiveData.observeForever(this@FragmentViewBindingDelegate)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        fragment.viewLifecycleOwnerLiveData.removeObserver(this@FragmentViewBindingDelegate)
                        fragment.lifecycle.removeObserver(this)
                    }
                    else -> {}
                }
            }
        })
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner?) {
        binding = if (viewLifecycleOwner == null) {
            null
        } else {
            factory(fragment.requireView())
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return binding
            ?: throw IllegalAccessException("Binding should not be accessed after it is destroyed")
    }
}

inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline factory: (View) -> T): FragmentViewBindingDelegate<T> {
    return FragmentViewBindingDelegate(this, factory)
}