package com.example.whatisup.src.ui

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.whatisup.R
import com.example.whatisup.src.ui.adapter.ReminderAdapter
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.reminder_fragment_layout.*

@AndroidEntryPoint
class ReminderFragment: androidx.fragment.app.Fragment() {

    private val reminderVm: ReminderViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reminder_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        reminder_recycler_view.layoutManager = GridLayoutManager(requireContext(), 2)
        val reminderAdapter = ReminderAdapter(requireContext(), listOf(), reminderVm)
        reminder_recycler_view.adapter = reminderAdapter

        reminderVm.state().observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                reminderAdapter.update(data.reminders)
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        reminderVm.getAllReminders()
    }
}