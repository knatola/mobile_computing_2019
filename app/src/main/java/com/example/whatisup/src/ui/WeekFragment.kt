package com.example.whatisup.src.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatisup.R
import com.example.whatisup.src.ui.adapter.DayActivityAdapter
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory
import com.example.whatisup.src.utils.Injection
import com.example.whatisup.src.utils.stringDate
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.week_fragment_layout.*

private const val TAG = "WeekFragment"

class WeekFragment: Fragment() {

    private lateinit var viewModel: DayActivityViewModel
    private lateinit var viewModelFactory: DayActivityViewModelFactory
    private lateinit var dayAdapter: DayActivityAdapter
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = Injection.provideDayActivityVmFactory(requireContext())
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(DayActivityViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.week_fragment_layout, container, false)
    }

    @SuppressLint
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        day_activities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        dayAdapter = DayActivityAdapter(listOf(), requireActivity() as AppCompatActivity)
        day_activities.adapter = dayAdapter

        forward_button.setOnClickListener {
            viewModel.setActivities(true)
        }

        back_button.setOnClickListener {
            viewModel.setActivities(false)
        }

        viewModel.state().observe(this, Observer {
            it?.let { state ->
                dayAdapter.update(state.activities)
                graph_view.setDataSet(state.activities)
                if (state.activities.isNotEmpty()) {
                    week_header.text = "${stringDate(state.activities[0].date)} - ${stringDate(state.activities[state.activities.lastIndex].date)}"
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}