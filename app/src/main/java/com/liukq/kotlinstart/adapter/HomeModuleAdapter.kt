package com.liukq.kotlinstart.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.liukq.kotlinstart.R
import com.liukq.kotlinstart.model.bean.HomeServiceItem
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
            SnackBarUtils.showSnackbar(parent, "pos=$pos")
        }
    }

/*switch (fun) {
    //直播
    case "hot_video":
    ActivityClass = HotBroadcastActivity.class;
    break;
    //问政
    case "town":
    ActivityClass = MunicipalActivity.class;
    break;
    //百姓视角
    case "town_pview_video":
    ActivityClass = PVideoActivity.class;
    break ;
    //新闻
    case "news":
    ActivityClass = NewsNActivity.class;
    break;
    //优品
    case "excellent":
    ActivityClass = ExcellentActivity.class;
    break;
    //旅游
    case "tour":
    ActivityClass = TravelActivity.class;
    break;
    //商机
    case "opportunity":
    ActivityClass = BusinessNActivity.class;
    break;
    //讲堂
    case "cellect":
    ActivityClass = ForumActivity.class;
    break;
    //收藏
    case "collect":
    case "collect_museum":
    case "collect_master":
    ActivityClass = ShouCangActivity.class;
    break;
    //名流
    case "washington_celebrity":
    ActivityClass=ChannelBaseActivity.class;
    break;
    //明星
    case "entertainment_celebrity":
    ActivityClass=EntertainmentActivity.class;
    break;
    //公益
    case "commonweal":
    ActivityClass = WelfareActivity.class;
    break;
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