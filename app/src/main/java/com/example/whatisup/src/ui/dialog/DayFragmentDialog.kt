package com.example.whatisup.src.ui.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatisup.R

class DayFragmentDialog(): androidx.fragment.app.DialogFragment() {

    companion object {

        fun getInstance(date: Long): DayFragmentDialog {
            val f = DayFragmentDialog()
            val args = Bundle()
            args.putLong("date", date)
            f.arguments = args

            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog)
    }
}