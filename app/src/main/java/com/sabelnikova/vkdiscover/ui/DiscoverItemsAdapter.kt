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
import com.bumptech.glide.Glide
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.model.DiscoverItem
import java.text.SimpleDateFormat
import java.util.*

class DiscoverItemsAdapter : StackView.Adapter() {

    private val items = mutableListOf<DiscoverItem>()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var onExpandView: (() -> Unit)? = null

    var onHideView: (() -> Unit)? = null

    override fun createViewHolder(parent: ViewGroup): StackView.ViewHolder =
            DiscoverItemViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_post, parent, false))

    override fun getItemsCount(): Int = items.size

    override fun bindViewHolder(viewHolder: StackView.ViewHolder, position: Int) {
        (viewHolder as DiscoverItemViewHolder).bindView(items[position])
    }

    fun addItems(items: List<DiscoverItem>) {
        this.items.addAll(items)
        notifyDataSet()
    }

    fun setItems(items: List<DiscoverItem>) {
        this.items.clear()
        addItems(items)
    }

    inner class DiscoverItemViewHolder(view: View) : StackView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.textTv)
        val viewPager: ViewPager = view.findViewById(R.id.photoViewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.photoTabLayout)
        val userNameTv: TextView = view.findViewById(R.id.userNameTv)
        val dateTv: TextView = view.findViewById(R.id.dateTv)
        val avatarIv: ImageView = view.findViewById(R.id.avatarIv)
        val scrollView: LockableScrollView = view.findViewById(R.id.scrollView)
        val expandView: ViewGroup = view.findViewById(R.id.expandLayout)
        val expandTv: TextView = view.findViewById(R.id.expandTv)
        val expandIv: ImageView = view.findViewById(R.id.expandIv)
        val container: ViewGroup = view.findViewById(R.id.container)
        val skipTv: TextView = view.findViewById(R.id.skipTv)
        val likeTv: TextView = view.findViewById(R.id.likeTv)

        fun bindView(discoverItem: DiscoverItem) {
            scrollView.scrollable = false

            var scrollViewPrepared = false
            var expanded = false

            skipTv.alpha = 0f
            likeTv.alpha = 0f

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
                if (expanded) {
                    scrollView.smoothScrollTo(0, 0)
                    onHideView?.invoke()
                    val newBottomMargin = 200
                    val a = object : Animation() {
                        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                            val params = scrollView.layoutParams as FrameLayout.LayoutParams
                            params.bottomMargin = (newBottomMargin * interpolatedTime).toInt()
                            scrollView.layoutParams = params
                            if (interpolatedTime == 1f) {
                                tv.maxLines = 3
                                container.setPadding(0, 0, 0, 0)
                            }
                        }
                    }
                    a.duration = 1000
                    scrollView.startAnimation(a)

                    scrollView.scrollable = false
                    expandIv.rotation = 0f
                    expandTv.setText(R.string.expand)

                } else {
                    tv.maxLines = Int.MAX_VALUE
                    onExpandView?.invoke()
                    val newBottomMargin = 0
                    val a = object : Animation() {
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
                    expandIv.rotation = 180f
                    expandTv.setText(R.string.hide)
                }
                expanded = !expanded
            }

            tv.text = discoverItem.text

            userNameTv.text = discoverItem.owner?.getName()
            dateTv.text = dateFormat.format(Date(discoverItem.date))
            Glide.with(view.context)
                    .load(discoverItem.owner?.getPhoto())
                    .into(avatarIv)

            val adapter = PhotoViewPagerAdapter()
            discoverItem.attachments?.let {
                adapter.setData(discoverItem.attachments.mapNotNull { it.photo?.sizes?.find { it.type == "y" }?.url ?: it.video?.url})
                viewPager.adapter = adapter
                tabLayout.setupWithViewPager(viewPager)
                viewPager.visibility = View.VISIBLE
                tabLayout.visibility = View.VISIBLE
            } ?: run {
                viewPager.visibility = View.GONE
                tabLayout.visibility = View.GONE
            }
        }
    }
}