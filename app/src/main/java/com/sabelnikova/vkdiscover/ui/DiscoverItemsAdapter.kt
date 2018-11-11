package com.sabelnikova.vkdiscover.ui

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.TabLayout
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.model.Attachment
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

    fun getItem(position: Int) = items[position]

    inner class DiscoverItemViewHolder(view: View) : StackView.ViewHolder(view) {

        private val scrollView: LockableScrollView = view.findViewById(R.id.scrollView)
        private val container: ConstraintLayout = view.findViewById(R.id.container)

        private val textTv: TextView = view.findViewById(R.id.textTv)
        private val expandView: ViewGroup = view.findViewById(R.id.expandLayout)
        private val expandTv: TextView = view.findViewById(R.id.expandTv)
        private val expandIv: ImageView = view.findViewById(R.id.expandIv)

        private val viewPager: PhotoViewPager = view.findViewById(R.id.photoViewPager)
        private val tabLayout: TabLayout = view.findViewById(R.id.photoTabLayout)

        private val linkLayout: ViewGroup = view.findViewById(R.id.include)
        private val linkTitleTv: TextView = view.findViewById(R.id.titleTv)
        private val linkCaptionTv: TextView = view.findViewById(R.id.captionTv)

        private val userNameTv: TextView = view.findViewById(R.id.userNameTv)
        private val dateTv: TextView = view.findViewById(R.id.dateTv)
        private val avatarIv: ImageView = view.findViewById(R.id.avatarIv)

        val skipTv: TextView = view.findViewById(R.id.skipTv)
        val likeTv: TextView = view.findViewById(R.id.likeTv)

        fun bindView(discoverItem: DiscoverItem) {
            resetView()

            var expandViewPrepared = false
            var expanded = false

            view.viewTreeObserver.addOnGlobalLayoutListener {

                if (!expandViewPrepared) {
                    if (!textFits(textTv)) {
                        expandView.visibility = View.VISIBLE
                    }
                    expandViewPrepared = true
                }
            }

            expandView.setOnClickListener {
                animate(expanded)
                expanded = !expanded
            }

            textTv.text = discoverItem.text
            userNameTv.text = discoverItem.owner?.getName()
            dateTv.text = dateFormat.format(Date(discoverItem.date * 1000))
            Glide.with(view.context)
                    .load(discoverItem.owner?.getPhoto())
                    .into(avatarIv)

            setupAttachments(discoverItem.attachments)
        }

        private fun setupAttachments(attachments: List<Attachment>?) {
            if (attachments == null) {
                viewPager.visibility = View.GONE
                tabLayout.visibility = View.GONE
                linkLayout.visibility = View.GONE
                return
            }

            val link = attachments.asSequence().mapNotNull { it.link }.firstOrNull()

            link?.let { link ->
                viewPager.visibility = View.GONE
                tabLayout.visibility = View.GONE

                linkCaptionTv.text = link.caption
                linkTitleTv.text = link.title
                linkLayout.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                    view.context.startActivity(browserIntent)
                }
                linkLayout.visibility = View.VISIBLE
            } ?: run {
                linkLayout.visibility = View.GONE
                val photos = attachments.mapNotNull {
                    it.photo?.getPhotoUrl() ?: it.video?.url
                }
                if (photos.isEmpty()) {
                    viewPager.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                } else {
                    if (photos.size == 1) {
                        tabLayout.visibility = View.GONE
                        viewPager.enableSidesClick = false
                    } else {
                        tabLayout.visibility = View.VISIBLE
                        viewPager.enableSidesClick = true
                    }
                    viewPager.visibility = View.VISIBLE
                    val adapter = PhotoViewPagerAdapter()
                    adapter.setData(photos)
                    viewPager.adapter = adapter
                    tabLayout.setupWithViewPager(viewPager)
                }
            }
        }

        private fun animate(expanded: Boolean) {
            val constraintSet = ConstraintSet()
            val transition = ChangeBounds()
            transition.interpolator = AccelerateDecelerateInterpolator()
            transition.duration = 500
            if (expanded) {
                constraintSet.clone(container)
                constraintSet.constrainHeight(R.id.textTv, ConstraintSet.MATCH_CONSTRAINT)
                scrollView.scrollable = false
                expandIv.rotation = 0f
                expandTv.setText(R.string.expand)
                scrollView.smoothScrollTo(0, 0)
                Handler().postDelayed({
                    TransitionManager.beginDelayedTransition(container, transition)
                    constraintSet.applyTo(container)
                }, 300)
            } else {
                constraintSet.clone(container)
                constraintSet.constrainHeight(R.id.textTv, ConstraintSet.WRAP_CONTENT)
                scrollView.scrollable = true
                expandIv.rotation = 180f
                expandTv.setText(R.string.hide)
                TransitionManager.beginDelayedTransition(container, transition)
                constraintSet.applyTo(container)
            }
        }

        private fun resetView() {
            val constraintSet = ConstraintSet()
            constraintSet.clone(view.context, R.layout.layout_post)
            constraintSet.applyTo(container)

            scrollView.scrollable = false

            expandIv.rotation = 0f
            expandTv.setText(R.string.expand)
            expandView.visibility = View.GONE

            skipTv.alpha = 0f
            likeTv.alpha = 0f
        }

        private fun textFits(textView: TextView): Boolean {
            val textHeight = textView.lineCount * textView.lineHeight
            return textHeight <= textView.height
        }
    }
}