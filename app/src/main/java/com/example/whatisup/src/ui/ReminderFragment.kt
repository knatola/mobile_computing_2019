package com.example.whatisup.src.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatisup.R
import com.example.whatisup.src.ui.adapter.ReminderAdapter
import com.example.whatisup.src.ui.adapter.ReminderViewHolder
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel
import com.example.whatisup.src.utils.Injection
import kotlinx.android.synthetic.main.reminder_fragment_layout.*

class ReminderFragment: Fragment() {

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

        reminder_recycler_view.layoutManager = GridLayoutManager(requireContext(), 2)
        val reminderAdapter = ReminderAdapter(requireContext(), listOf())
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