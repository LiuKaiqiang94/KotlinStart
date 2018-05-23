package com.liukq.kotlinstart.model.bean

/**
 * Created by lkq on 2018/5/22.
 */

data class BaseModel<out T>(val msg: String? = null,
                            val debugMsg: String? = null,
                            val retCode: Int,
                            val isSuccess: Boolean,
                            val retObj: T)

data class HomeServiceModel(val retCode: Int,
                            val retObj: List<HomeServiceItem>)

data class HomeServiceItem(val title: String,
                           val id: String,
                           val pic: String,
                           val `fun`: String)