package com.sabelnikova.vkdiscover.ui

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.model.DiscoverItem
import java.text.SimpleDateFormat
import java.util.*

class MyAdapter: StackView.Adapter<DiscoverItem>(){

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var onExpandView: (() -> Unit)? = null
    var onHideView: (() -> Unit)? = null

    override fun getView(parent: ViewGroup, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        val tv = view.findViewById<TextView>(R.id.textTv)
        val viewPager = view.findViewById<ViewPager>(R.id.photoViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.photoTabLayout)
        val userNameTv = view.findViewById<TextView>(R.id.userNameTv)
        val dateTv = view.findViewById<TextView>(R.id.dateTv)
        val avatarIv = view.findViewById<ImageView>(R.id.avatarIv)
        val scrollView = view.findViewById<LockableScrollView>(R.id.scrollView)
        val expandView = view.findViewById<View>(R.id.expandTv)
        val expandTv = view.findViewById<TextView>(R.id.expandTv)
        val expandIv = view.findViewById<View>(R.id.expandIv)
        val container = view.findViewById<ViewGroup>(R.id.container)

        var scrollViewPrepared = false

        var expanded = false

        scrollView.scrollable = false

        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (!scrollViewPrepared) {
                val childHeight = container.height
                val isScrollable = scrollView.height < childHeight

                if (isScrollable) {
                    tv.maxLines = 3
                    expandView.visibility = View.VISIBLE
                }
                scrollViewPrepared = true
            }
        }

        expandView.setOnClickListener {
            if (expanded){
                scrollView.smoothScrollTo(0, 0)
                onHideView?.invoke()
                val newBottomMargin = 200
                val a = object : Animation(){
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        val params = scrollView.layoutParams as FrameLayout.LayoutParams
                        params.bottomMargin = (newBottomMargin * interpolatedTime).toInt()
                        scrollView.layoutParams = params
                        if (interpolatedTime == 1f){
                            tv.maxLines = 3
                            container.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                a.duration = 1000
                scrollView.startAnimation(a)

                scrollView.scrollable = false
                expandIv?.rotation = 0f
                expandTv?.setText(R.string.expand)

            } else {
                tv.maxLines = Int.MAX_VALUE
                onExpandView?.invoke()
                val newBottomMargin = 0
                val a = object : Animation(){
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        val params = scrollView.layoutParams as FrameLayout.LayoutParams
                        params.bottomMargin = (newBottomMargin * interpolatedTime).toInt()
                        scrollView.layoutParams = params
                    }
                }
                a.duration = 1000
                scrollView.startAnimation(a)
                container.setPadding(0, 0, 0, 200)

                scrollView.scrollable = true
                expandIv?.rotation = 180f
                expandTv?.setText(R.string.hide)
            }
            expanded = !expanded
        }

        tv.text = getItem(position).text

        userNameTv.text = "Иван Иванов"
        dateTv.text = dateFormat.format(Date(getItem(position).date))
        avatarIv.setImageResource(R.drawable.ic_like_36)

        val adapter = PhotoViewPagerAdapter()
        adapter.setData(getItem(position).attachments.map { it.photo.accessKey })
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        return view
    }
}