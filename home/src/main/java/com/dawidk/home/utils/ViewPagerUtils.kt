package com.dawidk.home.utils

import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.setUpInfinityScroll(originalItemCount: () -> Int) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                when (this@setUpInfinityScroll.currentItem) {
                    0 -> this@setUpInfinityScroll.setCurrentItem(
                        originalItemCount(),
                        false
                    )
                    adapter?.itemCount?.minus(1) -> this@setUpInfinityScroll.setCurrentItem(
                        originalItemCount() - 1,
                        false
                    )
                }
            }
        }
    })
}