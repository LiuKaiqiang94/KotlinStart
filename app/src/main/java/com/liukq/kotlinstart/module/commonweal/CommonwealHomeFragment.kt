package com.liukq.kotlinstart.module.commonweal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.model.bean.CommonwealBannerItem
import com.liukq.kotlinstart.model.service.CommonwealService
import com.liukq.kotlinstart.utils.SnackBarUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_commonwel_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by lkq on 2018/5/23.
 */
class CommonwealHomeFragment : Fragment() {

    private var bannerList: ArrayList<CommonwealBannerItem> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_commonwel_home, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        doAsync {
            val data = CommonwealService.getBannerData()
            uiThread {
                if (null == data) {
                    SnackBarUtils.showSnackbar(action_bar_root, "请求失败")
                    return@uiThread
                }
                bannerList.addAll(data)
                banner.setData(R.layout.item_banner, bannerList, null)
                banner.setmAdapter({ _, view, position ->
                    val iv = view.findViewById(R.id.iv_pager_adapter) as ImageView
                    iv.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(context).load(bannerList[position].pic)
                            .into(iv)
                })
            }
        }
    }

    private fun initView() {

    }

}
