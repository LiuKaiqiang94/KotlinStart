package com.liukq.kotlinstart.utils

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by lkq on 2018/1/30.
 * 为RecyclerView设置verticalSpacing
 */

class SpacesItemDecoration(private val mVerticalSpacing: Int, private val mHorizontalSpacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.left = mHorizontalSpacing
        outRect.right = mHorizontalSpacing
        outRect.bottom = mVerticalSpacing
        outRect.top = mVerticalSpacing
    }
}
