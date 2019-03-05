package com.example.whatisup.src.ui.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.ui.DayFragment
import com.example.whatisup.src.ui.SECOND_DAY_FRAG_TAG
import com.example.whatisup.src.ui.WEEK_FRAGMENT_TAG
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.getActivityIcon
import com.example.whatisup.src.utils.getEmojiDrawable

private const val TAG = "DayActivityAdapter"

class DayActivityAdapter(var list: List<DayActivity>,
                         val context: AppCompatActivity): RecyclerView.Adapter<DayActivityViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DayActivityViewHolder, position: Int) {
        val activity = list[position]
        holder.dateText.text = TimeUtils.formatDate(activity.date.toString())
        holder.emoji.setImageDrawable(getEmojiDrawable(activity.emoji, context))

        if (!activity.activities.isEmpty()) {
            holder.firstActivity.setImageDrawable(getActivityIcon(activity.activities[0].type, context))
        }

        holder.rowLinear.setOnClickListener {
            Log.d(TAG, "starting dialog with day activity")

            val ft = context.supportFragmentManager.beginTransaction()
            val current = context.supportFragmentManager.findFragmentByTag(WEEK_FRAGMENT_TAG)
            val fragment = DayFragment()
            val args = Bundle()
            args.putLong("date", activity.date)
            fragment.arguments = args
            ft.add(R.id.main_framelayout, fragment, SECOND_DAY_FRAG_TAG)
            ft.hide(current!!)
            ft.addToBackStack(null)
            ft.commit()
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
    val rowLinear = view.findViewById<LinearLayout>(R.id.day_activity_linear)
    val dateText = view.findViewById<TextView>(R.id.day_activity_date)
    val emoji = view.findViewById<ImageView>(R.id.day_activity_emoji)
    val firstActivity = view.findViewById<ImageView>(R.id.day_activity_first)
}