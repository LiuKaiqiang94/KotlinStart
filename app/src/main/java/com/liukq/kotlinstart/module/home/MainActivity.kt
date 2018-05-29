package com.liukq.kotlinstart.module.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.model.bean.HomeServiceItem
import com.liukq.kotlinstart.model.service.HomeService
import com.liukq.kotlinstart.module.city.CityActivity
import com.liukq.kotlinstart.utils.SnackBarUtils
import com.liukq.kotlinstart.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    private var mData: ArrayList<HomeServiceItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        loadData()
    }

    private fun initView() {
        home_rv.layoutManager = GridLayoutManager(this, 2)
        home_rv.addItemDecoration(SpacesItemDecoration(10, 0))
        home_rv.adapter = HomeModuleAdapter(mData)
    }

    private fun loadData() {
        doAsync {
            val data = HomeService.getData()
            uiThread {
                if (null == data) {
                    SnackBarUtils.showSnackbar(action_bar_root, "请求失败")
                    return@uiThread
                }
                mData.addAll(data)
                home_rv.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, CityActivity::class.java))
        return true
    }
}
