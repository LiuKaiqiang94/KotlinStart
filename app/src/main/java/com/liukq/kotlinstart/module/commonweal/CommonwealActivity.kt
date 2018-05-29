package com.liukq.kotlinstart.module.commonweal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.module.home.TempFragment
import kotlinx.android.synthetic.main.activity_commonweal.*

/**
 * Created by lkq on 2018/5/22.
 */
class CommonwealActivity : AppCompatActivity() {

    private val fragments: Array<Fragment> = arrayOf(
            CommonwealHomeFragment(),
            TempFragment("资讯"),
            TempFragment("机构"),
            TempFragment("消息"),
            TempFragment("个人中心"))
    private var mCurrentIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commonweal)
        initView()
        initListener()
    }

    private fun initListener() {
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            for (j in 0..radio_group.childCount) {
                if (checkedId == radio_group.getChildAt(j).id) {
                    changeFragment(j)
                    break
                }
            }
        }
    }

    private fun changeFragment(index: Int) {
        if (index != mCurrentIndex) {
            if (mCurrentIndex == -1) mCurrentIndex = 0
            val trx = supportFragmentManager.beginTransaction()
            trx.hide(fragments[mCurrentIndex])
            if (!fragments[index].isAdded) {
                trx.add(R.id.fragment, fragments[index])
            }
            trx.show(fragments[index]).commit()
            mCurrentIndex = index
        }
    }

    private fun initView() {
        title = getString(R.string.commonweal)
        setRadioGroupCheck(0)
    }

    private fun setRadioGroupCheck(i: Int) {
        (radio_group.getChildAt(i) as RadioButton).isChecked = true
        changeFragment(i)
    }
}
