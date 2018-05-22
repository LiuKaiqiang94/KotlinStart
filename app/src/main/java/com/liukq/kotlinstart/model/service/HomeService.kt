package com.liukq.kotlinstart.model.service

import com.google.gson.Gson
import com.liukq.kotlinstart.model.bean.HomeServiceItem
import com.liukq.kotlinstart.model.bean.HomeServiceModel
import java.net.URL

/**
 * Created by lkq on 2018/5/22.
 */
class HomeService {
    companion object {
        //表示静态方法
        private const val baseUrl = "https://cms.loongscity.com/cityparlor-web/mobile/cityparlor/app/index/fun/newList"

        private fun urlBuild(page: Int, size: Int = 10): String {
            return "$baseUrl?page=$page&size=$size"
        }

        fun getData(): List<HomeServiceItem>? {
            val jsonString: String?
            try {
                jsonString = URL(baseUrl).readText()
            } catch (e: Exception) {
                return null
            }
            val data = Gson().fromJson(jsonString, HomeServiceModel::class.java)
            val list: List<HomeServiceItem> = data.retObj
            return if (list.isNotEmpty()) list else null
        }
    }
}