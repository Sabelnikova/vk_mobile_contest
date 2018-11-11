package com.sabelnikova.vkdiscover.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class PhotoViewPager : ViewPager {

    var enableSidesClick = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && enableSidesClick) {
            when {
                event.x < width / 4 -> {
                    currentItem -= 1
                    return true
                }
                event.x > width / 4 * 3 -> {
                    currentItem += 1
                    return true
                }
            }
        }
        return false
    }

}
