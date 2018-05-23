package com.liukq.kotlinstart.widget

import android.content.Context
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker

class LoongsCityBannerViewPager : ViewPager {

    private var mIsAllowUserScroll = true
    private var mAutoPlayDelegate: AutoPlayDelegate? = null

    private val xVelocity: Float
        get() {
            var xVelocity = 0f
            val viewpagerClass = ViewPager::class.java
            try {
                val velocityTrackerField = viewpagerClass.getDeclaredField("mVelocityTracker")
                velocityTrackerField.isAccessible = true
                val velocityTracker = velocityTrackerField.get(this) as VelocityTracker

                val activePointerIdField = viewpagerClass.getDeclaredField("mActivePointerId")
                activePointerIdField.isAccessible = true

                val maximumVelocityField = viewpagerClass.getDeclaredField("mMaximumVelocity")
                maximumVelocityField.isAccessible = true
                val maximumVelocity = maximumVelocityField.getInt(this)

                velocityTracker.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                xVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker, activePointerIdField.getInt(this))
            } catch (e: Exception) {
            }

            return xVelocity
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    /**
     * 设置ViewPager的滚动速度
     *
     * @param duration page切换的时间长度
     */
    fun setScrollDuration(duration: Int) {
        try {
            val scrollerField = ViewPager::class.java.getDeclaredField("mScroller")
            scrollerField.isAccessible = true
            scrollerField.set(this, LoongsCityBannerScroller(context, duration))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 切换到指定索引的页面，主要用于自动轮播
     *
     * @param position
     */
    fun setBannerCurrentItemInternal(position: Int) {
        val viewpagerClass = ViewPager::class.java
        try {
            val setCurrentItemInternalMethod = viewpagerClass.getDeclaredMethod("setCurrentItemInternal", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            setCurrentItemInternalMethod.isAccessible = true
            setCurrentItemInternalMethod.invoke(this, position, true, true)
            ViewCompat.postInvalidateOnAnimation(this)
        } catch (e: Exception) {
        }

    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param iSallowUserScroll
     */
    fun setIsAllowUserScroll(iSallowUserScroll: Boolean) {
        mIsAllowUserScroll = iSallowUserScroll
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mIsAllowUserScroll) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {

        return if (mIsAllowUserScroll) {
            if (mAutoPlayDelegate != null
                    && (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP)) {
                mAutoPlayDelegate!!.handleAutoPlayActionUpOrCancel(xVelocity)
                false
            } else {
                super.onTouchEvent(ev)
            }
        } else {
            false
        }
    }

    fun setAutoPlayDelegate(autoPlayDelegate: AutoPlayDelegate) {
        mAutoPlayDelegate = autoPlayDelegate
    }

    interface AutoPlayDelegate {
        fun handleAutoPlayActionUpOrCancel(xVelocity: Float)
    }
}