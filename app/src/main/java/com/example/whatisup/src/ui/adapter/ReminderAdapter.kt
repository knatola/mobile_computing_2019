package com.example.whatisup.src.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.model.Reminder
import com.example.whatisup.src.data.model.getReminderDrawable
import com.example.whatisup.src.data.model.getReminderHeader
import com.example.whatisup.src.data.model.getReminderText
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel
import com.example.whatisup.src.utils.stringDate

class ReminderAdapter(private val context: Context,
                      private var data: List<Reminder>,
                      private val vm: ReminderViewModel)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ReminderViewHolder>() {

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = data[position]

        // Some hackyness needed to achieve "uneven" grid layout effect, still the buttons don't align perfectly :(
        when {
            position == 0 -> {
                val params = holder.baseView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(4, 200, 4, 4)
                holder.baseView.requestLayout()
                val btnParams = holder.btn.layoutParams as RelativeLayout.LayoutParams
                btnParams.bottomMargin = 0
            }
            position == 1-> {
                val params = holder.baseView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(4, 4, 4, 280)
                holder.baseView.requestLayout()
//                val btnParams = holder.btn.layoutParams as RelativeLayout.LayoutParams
//                btnParams.bottomMargin = 0
            }
            position % 2 != 0 -> {
                val params = holder.baseView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(4, -266, 4, 280)
                holder.baseView.requestLayout()
                val btnParams = holder.btn.layoutParams as RelativeLayout.LayoutParams
                btnParams.bottomMargin = 0
            }
//            position % 2 == 0 -> {
//                val params = holder.baseView.layoutParams as ViewGroup.MarginLayoutParams
//                params.setMargins(4, -40, 4, 40)
//                holder.baseView.requestLayout()
//            }
        }

        holder.bind(reminder, context)

        holder.btn.setOnClickListener {
            vm.deleteReminder(reminder)
        }
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
        notifyDataSetChanged()
    }
}

class ReminderViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    val baseView = v
    val header = v.findViewById<TextView>(R.id.reminder_header)
    val icon = v.findViewById<ImageView>(R.id.reminder_icon)
    val text = v.findViewById<TextView>(R.id.reminder_text)
    val date = v.findViewById<TextView>(R.id.reminder_date)
    val btn = v.findViewById<Button>(R.id.reminder_delete)

    fun bind(reminder: Reminder, context: Context) {
        this.header.text = getReminderHeader(reminder.type, context)
        this.text.text = getReminderText(reminder.type, context)
        this.icon.setImageDrawable(getReminderDrawable(reminder.type, context))
        this.date.text = stringDate(reminder.date)
    }
}