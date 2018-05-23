package com.liukq.kotlinstart.widget

import android.content.Context
import android.widget.Scroller

class LoongsCityBannerScroller internal constructor(context: Context, private val mDuration: Int//值越大，滑动越慢
) : Scroller(context) {

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }
}
