package com.sabelnikova.vkdiscover.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.absoluteValue

class StackView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {
        const val MAX_TRANSLATION_BEFORE_SWIPE = 400
    }

    enum class SwipeDirection {
        LEFT,
        RIGHT
    }

    var adapter: StackView.Adapter? = null
        set(value) {
            field = value
            field?.onDataSet = {
                getFirstDataFromAdapter(field)
            }
            getFirstDataFromAdapter(field)
        }
    var swipeEnabled = true
    var dataSet = false

    var frontViewHolder: ViewHolder? = null
        get() {
            if (field == null) {
                field = adapter?.createViewHolder(this)
            }
            return field
        }

    var backViewHolder: ViewHolder? = null
        get() {
            if (field == null) {
                field = adapter?.createViewHolder(this)
            }
            return field
        }

    private var frontView: View? = null
        get() = frontViewHolder?.view

    private var backView: View? = null
        get() = backViewHolder?.view

    private var lastX: Float? = null

    private var currentIndex = 0

    private var currentDirection: SwipeDirection? = null

    var onSwipeProgress: ((progress: Float, direction: SwipeDirection) -> Unit)? = null
    var onStartSwipe: ((position: Int, direction: SwipeDirection) -> Unit)? = null
    var onStopSwipe: ((position: Int, direction: SwipeDirection) -> Unit)? = null
    var onItemAppearInBack: ((position: Int) -> Unit)? = null

    fun swipe(direction: SwipeDirection) =
            when (direction) {
                SwipeDirection.LEFT -> swipe(-1)
                SwipeDirection.RIGHT -> swipe(1)
            }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!swipeEnabled) return false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
            }
            MotionEvent.ACTION_UP -> {
                if (currentDirection != null) {
                    frontView?.animate()?.translationX(0f)
                    frontView?.animate()?.rotation(0f)
                    currentDirection?.let { direction -> onStopSwipe?.invoke(currentIndex, direction) }
                    lastX = null
                    currentDirection = null
                    return true
                }
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                lastX?.let { x1 ->
                    event.x.let { x2 ->
                        val delta = x2 - x1
                        val sign = if (delta > 0) 1 else -1
                        if (delta.absoluteValue > 10) {
                            currentDirection = if (delta > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                            currentDirection?.let { direction -> onStartSwipe?.invoke(currentIndex, direction) }
                            frontView?.translationX = delta * 1.2f
                            frontView?.rotation = delta / 40
                            frontView?.invalidate()
                            if (frontView?.translationX?.absoluteValue ?: 0f >= MAX_TRANSLATION_BEFORE_SWIPE) {
                                swipe(sign)
                            }
                            currentDirection?.let {
                                onSwipeProgress?.invoke(((frontView?.translationX ?: 0f) / MAX_TRANSLATION_BEFORE_SWIPE).absoluteValue, it)
                            }
                        }

                    }
                }
            }
        }
        return true
    }

    private fun setFront(view: View) {
        addView(view)
    }

    private fun setBack(view: View) {
        view.rotation = 0f
        view.translationX = 0f
        view.scaleX = 0.95f
        view.scaleY = 0.95f
        addView(view, 0)
    }

    private fun updateBack(position: Int) {
        backViewHolder?.let { adapter?.bindViewHolder(it, position) }
        backViewHolder?.view?.let { setBack(it) }
        onItemAppearInBack?.invoke(position)
    }

    private fun swapViewHolders() {
        val temp = frontViewHolder
        frontViewHolder = backViewHolder
        backViewHolder = temp
    }

    private fun swipe(sign: Int) {
        val windowSize = Point()
        (context as? Activity)?.windowManager?.defaultDisplay?.getSize(windowSize)

        backView?.animate()?.scaleX(1f)
        backView?.animate()?.scaleY(1f)
        val rotationAnimation = ObjectAnimator.ofFloat(frontView, "rotation", sign * 8f)
        val swipeAnimation = ObjectAnimator.ofFloat(frontView, "translationX", sign * windowSize.x * 1f)

        val swipeSet = AnimatorSet()
        swipeSet.playTogether(rotationAnimation, swipeAnimation)
        swipeSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                removeView(frontView)
                swapViewHolders()
                if (adapter?.getItemsCount() ?: 0 > currentIndex + 2) {
                    updateBack(currentIndex + 2)
                    currentIndex++
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}


        })
        swipeSet.start()
        lastX = null
        currentDirection = null
    }

    private fun getFirstDataFromAdapter(adapter: Adapter?) {
        if (dataSet) return

        if (adapter?.getItemsCount() != 0) {
            frontViewHolder?.view?.let { setFront(it) }
            frontViewHolder?.let { adapter?.bindViewHolder(it, 0) }
            dataSet = true
        }

        if (adapter?.getItemsCount() ?: 0 > 1) {
            backViewHolder?.view?.let { setBack(it) }
            backViewHolder?.let { adapter?.bindViewHolder(it, 1) }
        }
    }


    abstract class Adapter {

        var onDataSet: (() -> Unit)? = null

        abstract fun createViewHolder(parent: ViewGroup): ViewHolder

        abstract fun getItemsCount(): Int

        abstract fun bindViewHolder(viewHolder: ViewHolder, position: Int)

        fun notifyDataSet() {
            onDataSet?.invoke()
        }
    }

    abstract class ViewHolder(var view: View)
}