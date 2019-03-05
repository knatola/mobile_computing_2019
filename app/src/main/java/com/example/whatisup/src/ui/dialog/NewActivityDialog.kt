package com.example.whatisup.src.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.whatisup.R
import com.example.whatisup.src.data.model.PhysicalActivity
import kotlinx.android.synthetic.main.add_activity_dialog_layout.*
import java.util.concurrent.TimeUnit

class NewActivityDialog(context: Context): Dialog(context) {

    private lateinit var callBack: NewActivityCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity_dialog_layout)
        val adapter = ArrayAdapter.createFromResource(context, R.array.activites, android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activity_type_spinner.adapter = adapter

        activity_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> activity_icon.setImageDrawable(context.getDrawable(R.drawable.activity_walking))
                    1 -> activity_icon.setImageDrawable(context.getDrawable(R.drawable.activity_running))
                    2 -> activity_icon.setImageDrawable(context.getDrawable(R.drawable.activity_gym))
                    3 -> activity_icon.setImageDrawable(context.getDrawable(R.drawable.activity_swimming))
                    4 -> activity_icon.setImageDrawable(context.getDrawable(R.drawable.activity_cycling))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        activity_save_btn.setOnClickListener {
            val hours = activity_hour_input.text.toString()
            val minutes = activity_minute_input.text.toString()

            if (hours != "" && minutes != "") {
                val type = activity_type_spinner.selectedItemPosition
                val duration = TimeUnit.MINUTES.toMillis(minutes.toLong()) + TimeUnit.HOURS.toMillis(minutes.toLong())
//                val duration = "$hours:$minutes"
                val newActivity = PhysicalActivity(type, duration)
                callBack.onNewActivity(newActivity)
                this.cancel()
            } else {
                activity_save_btn.setTextColor(context.getColor(R.color.red))
            }
        }
    }

    fun setCallback(callBack: NewActivityCallback) {
        this.callBack = callBack
    }

    interface NewActivityCallback {
        fun onNewActivity(activity: PhysicalActivity)
    }
}

