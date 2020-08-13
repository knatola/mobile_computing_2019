package com.example.whatisup.src.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatisup.R
import com.example.whatisup.src.ui.DayFragment

class DayActivityDialog(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_view_fragment_layout)
        (ownerActivity as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .add(R.id.day_activity_container, DayFragment())
            .commit()
    }
}