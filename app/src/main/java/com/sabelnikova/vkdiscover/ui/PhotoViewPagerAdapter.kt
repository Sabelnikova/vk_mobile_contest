package com.sabelnikova.vkdiscover.ui

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide

class PhotoViewPagerAdapter : PagerAdapter() {

    private var photos: List<String>? = null

    override fun getCount(): Int {
        return photos?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val imageView = ImageView(container.context)

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        Glide.with(container.context)
                .asBitmap()
                .load(photos?.get(position))
                .into(imageView)

        container.addView(imageView)
        container.requestLayout()

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun setData(photos: List<String>) {
        this.photos = photos
        notifyDataSetChanged()
    }
}