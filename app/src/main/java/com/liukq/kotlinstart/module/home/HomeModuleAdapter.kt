package com.liukq.kotlinstart.module.home

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.model.bean.HomeServiceItem
import com.liukq.kotlinstart.module.commonweal.CommonwealActivity
import com.liukq.kotlinstart.utils.SnackBarUtils
import org.jetbrains.anko.find

/**
 * Created by lkq on 2018/5/22.
 */
class HomeModuleAdapter(var items: List<HomeServiceItem>) : RecyclerView.Adapter<HomeModuleAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.imageView)
                .load(items[position].pic)
                .into(holder.imageView)
        holder.titleTv.text = items[position].title
        holder.titleTv.paint.isFakeBoldText = true
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_home_module, parent, false)
        return MyViewHolder(view).listen { pos, _ ->
            val f = items[pos].`fun`
            when (f) {
                "commonweal" -> parent.context.startActivity(Intent(parent.context, CommonwealActivity::class.java))
                else -> SnackBarUtils.showSnackbar(parent, "pos=$pos,fun=$f")
            }
        }
    }

/*switch (fun) {
    //直播
    case "hot_video":
    //问政
    case "town":
    //百姓视角
    case "town_pview_video":
    //新闻
    case "news":
    //优品
    case "excellent":
    //旅游
    case "tour":
    //商机
    case "opportunity":
    //讲堂
    case "cellect":
    //收藏
    case "collect":
    case "collect_museum":
    case "collect_master":
    //名流
    case "washington_celebrity":
    //明星
    case "entertainment_celebrity":
    //公益
    case "commonweal":
}*/


    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val titleTv: TextView = item.find(R.id.title)
        val imageView: ImageView = item.find(R.id.image_view)
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}