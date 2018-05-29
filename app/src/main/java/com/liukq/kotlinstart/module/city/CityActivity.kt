package com.liukq.kotlinstart.module.city

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.module.home.TempFragment
import com.liukq.kotlinstart.utils.SnackBarUtils
import kotlinx.android.synthetic.main.activity_city.*
import org.jetbrains.anko.contentView

class CityActivity : AppCompatActivity() {

    private val tabs: Array<Fragment> = arrayOf(DomesticFragment(), TempFragment("国际城市"))
    private var titles: Array<String> = arrayOf("国内", "国际")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        initView()
        test()
    }

    private fun test() {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        SnackBarUtils.showSnackbar(contentView as ViewGroup, "最大堆内存${manager.memoryClass},maxHeapSize${manager.largeMemoryClass}")
    }

    private fun initView() {
        title = getString(R.string.switch_city)
        mViewPager.offscreenPageLimit = tabs.size
        mViewPager.adapter = CityPageAdapter(supportFragmentManager)
        mTabLayout.setupWithViewPager(mViewPager)
    }

    private inner class CityPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return tabs[position]
        }

        override fun getCount(): Int {
            return tabs.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}
