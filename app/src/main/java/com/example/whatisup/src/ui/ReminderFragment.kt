package com.example.whatisup.src.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatisup.R
import com.example.whatisup.src.ui.adapter.ReminderAdapter
import com.example.whatisup.src.ui.adapter.ReminderViewHolder
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel
import com.example.whatisup.src.utils.Injection
import kotlinx.android.synthetic.main.reminder_fragment_layout.*

class ReminderFragment: androidx.fragment.app.Fragment() {

    private lateinit var reminderVm: ReminderViewModel
    private lateinit var reminderVmFactory: ReminderViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reminderVmFactory = Injection.provideReminderVmFactory(requireContext())
        reminderVm = ViewModelProviders.of(requireActivity(), reminderVmFactory).get(ReminderViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reminder_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        reminder_recycler_view.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        val reminderAdapter = ReminderAdapter(requireContext(), listOf(), reminderVm)
        reminder_recycler_view.adapter = reminderAdapter

        reminderVm.state().observe(this, Observer {
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