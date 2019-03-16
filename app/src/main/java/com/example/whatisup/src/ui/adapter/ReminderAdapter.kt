package com.example.whatisup.src.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.model.Reminder

class ReminderAdapter(private val context: Context, private var data: List<Reminder>)
    : RecyclerView.Adapter<ReminderViewHolder>() {

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = data[position]

        holder.header.text = reminder.header
        holder.text.text = reminder.text

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ReminderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reminder_card_layout, parent, false)

        return ReminderViewHolder(view)
    }

    fun update(data: List<Reminder>) {
        this.data = data
    }
}

class ReminderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val header = v.findViewById<TextView>(R.id.reminder_header)
    val icon = v.findViewById<ImageView>(R.id.reminder_icon)
    val text = v.findViewById<TextView>(R.id.reminder_text)
}