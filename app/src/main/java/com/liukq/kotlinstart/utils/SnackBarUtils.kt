package com.liukq.kotlinstart.utils

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.liukq.kotlinstart.R

/**
 * Created by lkq on 2018/5/22.
 */
open class SnackBarUtils{
    companion object {
        fun showSnackbar(viewGroup: ViewGroup, text: String, duration: Int = 1000) {
            val snack = Snackbar.make(viewGroup, text, duration)
            snack.view.setBackgroundColor(ContextCompat.getColor(viewGroup.context, R.color.colorPrimary))
            snack.show()
        }
    }

}
