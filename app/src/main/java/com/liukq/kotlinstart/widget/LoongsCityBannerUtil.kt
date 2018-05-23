package com.liukq.kotlinstart.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue

object LoongsCityBannerUtil {

    /**
     * 设置背景选择器
     */
    fun getSelector(normalDraw: Drawable, pressedDraw: Drawable): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDraw)
        stateListDrawable.addState(intArrayOf(), normalDraw)
        return stateListDrawable
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.resources.displayMetrics).toInt()
    }

}
