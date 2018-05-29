package com.liukq.kotlinstart.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.liukq.kotlinstart.R

/**
 * Created by lkq on 2017/5/19.
 * 公益首页爱心数量View
 */

class LoveCountView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : LinearLayout(mContext, attrs) {

    init {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        setBackgroundColor(resources.getColor(R.color.gary_f7))
    }

    fun setCount(count: Int) {
        removeAllViews()
        if (count < 0)
            throw IllegalArgumentException("count can not less than 0")
        val countStr = count.toString() + ""
        addView(getNewTextView("已收到"))
        for (i in 0 until countStr.length) {
            addView(getNumTextView(countStr[i].toString()))
        }
        addView(getNewTextView("爱心"))
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getNumTextView(s: String): TextView {
        val textView = TextView(mContext)
        textView.gravity = Gravity.CENTER
        textView.text = s
        val layoutParams = LinearLayout.LayoutParams(60, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        layoutParams.setMargins(10, 14, 10, 14)
        textView.layoutParams = layoutParams
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.mipmap.love_count_bac))
        return textView
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getNewTextView(s: String): TextView {
        val textView = TextView(mContext)
        textView.text = s
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setTextColor(resources.getColor(R.color.black))
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = layoutParams
        return textView
    }
}
