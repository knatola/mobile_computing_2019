package com.example.whatisup.src.ui.adapter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.ui.CircleTransform
import com.example.whatisup.src.ui.view.HorizontalGraphView
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.getEmojiDrawable
import com.squareup.picasso.Picasso

private const val TAG = "DayActivityAdapter"

class DayActivityAdapter(var list: List<DayActivity>,
                         val context: AppCompatActivity): RecyclerView.Adapter<DayActivityViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DayActivityViewHolder, position: Int) {
        val activity = list[position]

        holder.bind(activity, context)

        if (!activity.activities.isEmpty()) {
//            holder.firstActivity.setImageDrawable(getActivityIcon(activity.activities[0].type, context))
        }

        holder.itemView.setOnClickListener {
            activity.expanded = !activity.expanded
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DayActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_activity_row, parent, false)
        return DayActivityViewHolder(view)
    }

    fun update(list: List<DayActivity>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class DayActivityViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val rowLinear = view.findViewById<RelativeLayout>(R.id.day_activity_linear)
    val dateText = view.findViewById<TextView>(R.id.day_activity_date)
    val image = view.findViewById<ImageView>(R.id.day_activity_image)
    val moreInfo = view.findViewById<RelativeLayout>(R.id.day_more_info)
    val graph = view.findViewById<HorizontalGraphView>(R.id.day_graph)
    val emoji = view.findViewById<ImageView>(R.id.day_activity_emoji)
    val caption = view.findViewById<TextView>(R.id.day_activity_caption)
    val expanded = view.findViewById<ImageView>(R.id.day_row_expanded)

    fun bind(activity: DayActivity, context: Context) {
        this.dateText.text = TimeUtils.formatDate(activity.date.toString())
        this.caption.text = if (activity.imageCaption != "") activity.imageCaption else "No caption"
        this.emoji.setImageDrawable(getEmojiDrawable(activity.emoji, context))

        Picasso.get().load(activity.imagePath).transform(CircleTransform()).into(this.image)

        when (activity.expanded) {
            true -> {
                moreInfo.visibility = View.VISIBLE
                graph.setData(activity.activities)
                graph.showTargets(false)
                expanded.setImageDrawable(context.getDrawable(R.drawable.ic_expand_less_black_24dp))
            }

            false -> {
                expanded.setImageDrawable(context.getDrawable(R.drawable.ic_expand_more_black_24dp))
                moreInfo.visibility = View.GONE
            }
        }
    }
}