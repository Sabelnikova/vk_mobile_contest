package com.sabelnikova.vkdiscover.ui

import android.animation.Animator
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

    enum class SwipeDirection {
        LEFT,
        RIGHT
    }

    var adapter: StackView.Adapter<*>? = null
        set(value) {
            field = value
            if (value?.getItemsCount() != 0) {
                value?.getView(this, 0)?.let { setFront(it) }
            }

            if (value?.getItemsCount() ?: 0 > 1) {
                value?.getView(this, 1)?.let { setBack(it) }
            }
        }

    var swipeEnabled = true

    private var frontView: View? = null

    private var backView: View? = null

    private var lastX: Float? = null

    private var currentIndex = 0

    private var currentDirection: SwipeDirection? = null

    var onStartSwipe: ((position: Int, direction: SwipeDirection) -> Unit)? = null
    var onStopSwipe: ((position: Int, direction: SwipeDirection) -> Unit)? = null

    fun getFrontView() = frontView

    fun swipe(direction: SwipeDirection) =
            when (direction) {
                SwipeDirection.LEFT -> swipe(-1)
                SwipeDirection.RIGHT -> swipe(1)
            }

    private fun setFront(view: View) {
        frontView = view
        addView(frontView)
    }

    private fun setBack(view: View) {
        backView = view
        backView?.scaleX = 0.95f
        backView?.scaleY = 0.95f
        addView(backView, 0)
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
                            if (frontView?.translationX?.absoluteValue ?: 0f >= 400) {
                                swipe(sign)
                            }
                        }

                    }
                }
            }
        }
        return true
    }

    private fun swipe(sign: Int) {
        val windowSize = Point()
        (context as? Activity)?.windowManager?.defaultDisplay?.getSize(windowSize)

        backView?.animate()?.scaleX(1f)
        backView?.animate()?.scaleY(1f)
        frontView?.animate()?.rotation(8f * sign)
        val swipeAnimation = ObjectAnimator.ofFloat(frontView, "translationX", sign * windowSize.x * 1f)
        swipeAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                removeView(frontView)
                frontView = backView
                if (adapter?.getItemsCount() ?: 0 > currentIndex + 2) {
                    adapter?.getView(this@StackView, currentIndex + 2)?.let { setBack(it) }
                    currentIndex++
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}


        })
        swipeAnimation?.start()
        lastX = null
        currentDirection = null
    }

    abstract class Adapter<T> {

        private val items = mutableListOf<T>()

        abstract fun getView(parent: ViewGroup, position: Int): View

        fun getItemsCount() = items.size

        fun getItem(position: Int) = items[position]

        fun addItems(items: List<T>) {
            this.items.addAll(items)
        }

        fun setItems(items: List<T>) {
            this.items.clear()
            addItems(items)
        }

    }
}