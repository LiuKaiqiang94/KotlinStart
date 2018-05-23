package com.liukq.kotlinstart.model.service

import com.google.gson.Gson
import com.liukq.kotlinstart.model.bean.CommonwealBannerItem
import com.liukq.kotlinstart.model.bean.CommonwealBannerModel
import java.net.URL

/**
 * Created by lkq on 2018/5/23.
 */
class CommonwealService {
    companion object {
        //表示静态方法
        private const val baseUrl = "https://cms.loongscity.com/cityparlor-web/mobile/cityparlor/sys/banner/list"

        private fun urlBuild(`fun`: String, area: String = "110100"): String {
            return "$baseUrl?fun=$`fun`&area=$area"
        }

        fun getBannerData(): List<CommonwealBannerItem>? {
            val jsonString: String?
            try {
                jsonString = URL(urlBuild("commonweal")).readText()
            } catch (e: Exception) {
                return null
            }
            val data = Gson().fromJson(jsonString, CommonwealBannerModel::class.java)
            val list: List<CommonwealBannerItem> = data.retObj
            return if (list.isNotEmpty()) list else null
        }
    }
}