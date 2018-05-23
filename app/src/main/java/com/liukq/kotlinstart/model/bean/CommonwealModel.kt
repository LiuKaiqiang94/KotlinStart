package com.liukq.kotlinstart.model.bean

/**
 * Created by lkq on 2018/5/23.
 */
data class CommonwealBannerModel(val retCode: Int,
                            val retObj: List<CommonwealBannerItem>)

data class CommonwealBannerItem(val id: String,
                           val pic: String,
                           val `fun`: String)