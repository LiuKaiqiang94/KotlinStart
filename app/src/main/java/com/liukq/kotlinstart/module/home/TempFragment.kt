package com.liukq.kotlinstart.module.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liukq.kotlinstart.R
import kotlinx.android.synthetic.main.fragment_temp.*

/**
 * Created by lkq on 2018/5/23.
 */
@SuppressLint("ValidFragment")
class TempFragment(private val title: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_temp, null)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempText.text = "todo:$title"
    }
}