package com.sabelnikova.vkdiscover.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class PhotoViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
