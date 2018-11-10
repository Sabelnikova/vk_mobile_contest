package com.sabelnikova.vkdiscover.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class LockableScrollView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {

    var scrollable = true

    override fun onTouchEvent(event: MotionEvent): Boolean =
            when (event.action) {
                MotionEvent.ACTION_DOWN -> scrollable && super.onTouchEvent(event)
                else -> super.onTouchEvent(event)
            }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return scrollable && super.onInterceptTouchEvent(event)
    }
}