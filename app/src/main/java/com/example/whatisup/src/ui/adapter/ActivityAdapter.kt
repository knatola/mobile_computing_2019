package com.example.whatisup.src.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.ui.dialog.NewActivityDialog
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.getActivityIcon
import com.example.whatisup.src.utils.getActivityText

private const val TAG = "ActivityAdapter"

class ActivityAdapter(var activityList: MutableList<PhysicalActivity>,
                      private val context: Context): RecyclerView.Adapter<ActivityViewHolder>() {

    init {
        activityList.add(0, PhysicalActivity(6, 0))
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(p0: ActivityViewHolder, p1: Int) {
        if (p1 == 0) {
            p0.activityName.text = context.getString(R.string.general_add)
            p0.activityDuration.text = context.getString(R.string.day_activity_name)
            p0.activityIcon.setImageDrawable(context.getDrawable(R.drawable.ic_add_black_24dp))
        } else {
            val activity = activityList[p1]
            p0.activityName.text = getActivityText(activity.type, context)
            p0.activityDuration.text = TimeUtils.getDurationBreakdown(activity.duration)
            p0.activityIcon.setImageDrawable(getActivityIcon(activity.type, context))
        }

        p0.activityLayout.setOnClickListener {
            if (p1 == 0) {
                val newDialog = NewActivityDialog(context)
                newDialog.setCallback(object: NewActivityDialog.NewActivityCallback{
                    override fun onNewActivity(activity: PhysicalActivity) {
                        Log.d(TAG, "New activity from dialog!")
                        activityList.add(activity)
                        notifyDataSetChanged()
                    }
                })
                newDialog.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.physical_activity_row, parent, false)
        return ActivityViewHolder(view)
    }

    fun update(list: List<PhysicalActivity>) {
        this.activityList.clear()
        activityList.add(0, PhysicalActivity(6, 0)) // first item should always be there, it will be the add button
        this.activityList.addAll(list)
        notifyDataSetChanged()
    }
}

class ActivityViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val activityLayout = v.findViewById<RelativeLayout>(R.id.activity_relative)
    val activityName = v.findViewById<TextView>(R.id.activity_name)
    val activityDuration = v.findViewById<TextView>(R.id.activity_duration)
    val activityIcon = v.findViewById<ImageView>(R.id.activity_icon)
}