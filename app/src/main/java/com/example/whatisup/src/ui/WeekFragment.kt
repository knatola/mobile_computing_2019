package com.example.whatisup.src.ui

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.whatisup.R
import com.example.whatisup.src.ui.adapter.DayActivityAdapter
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.src.utils.stringDate
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.week_fragment_layout.*

private const val TAG = "WeekFragment"

@AndroidEntryPoint
class WeekFragment: androidx.fragment.app.Fragment() {

    private val viewModel: DayActivityViewModel by viewModels()
    private lateinit var dayAdapter: DayActivityAdapter
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.week_fragment_layout, container, false)
    }

    @SuppressLint
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        day_activities.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )

        dayAdapter = DayActivityAdapter(listOf(), requireActivity() as AppCompatActivity)
        day_activities.adapter = dayAdapter

        forward_button.setOnClickListener {
            viewModel.setActivities(true)
        }

        back_button.setOnClickListener {
            viewModel.setActivities(false)
        }

        viewModel.state().observe(viewLifecycleOwner, Observer {
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