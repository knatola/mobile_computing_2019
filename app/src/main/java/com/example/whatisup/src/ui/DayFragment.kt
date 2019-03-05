package com.example.whatisup.src.ui

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.whatisup.R
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.data.common.Status
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.ui.adapter.ActivityAdapter
import com.example.whatisup.src.ui.adapter.EmojiAdapter
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory
import com.example.whatisup.src.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.day_view_fragment_layout.*

private const val TAG = "DayFragment"
private const val REQUEST_IMAGE_PICK = 11

class DayFragment: Fragment() {

    private lateinit var viewModel: DayActivityViewModel
    private lateinit var viewModelFactory: DayActivityViewModelFactory
    private lateinit var emojiAdapter: EmojiAdapter
    private lateinit var physActivityAdapter: ActivityAdapter

    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = Injection.provideDayActivityVmFactory(requireContext())
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(DayActivityViewModel::class.java)

        arguments?.let {
            date = arguments!!.getLong("date")
            viewModel.setActivity(date)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.day_view_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        activity_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        emoticon_wrapper.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        physActivityAdapter = ActivityAdapter(mutableListOf(), requireContext())
        emojiAdapter = EmojiAdapter(getEmojiList(), requireContext())
//        activity_recycler.adapter = physActivityAdapter
        emoticon_wrapper.adapter = emojiAdapter

        viewModel.currentDay.observe(this, Observer {
            it?.let { activity ->
                Log.d(TAG, "Updating day UI: ${activity}")
                emojiAdapter.setSelection(activity.emoji)
                (requireActivity() as MainActivity).setTitle(stringDate(activity.date))
                physActivityAdapter.update(activity.activities)
                today_mood_input.setText(activity.moodText)
                activity_graph.setData(activity.activities)
                Log.i(TAG, "loading image: ${activity.imagePath}")
                if (activity.imagePath != "")
                    Picasso.get().load(activity.imagePath).noPlaceholder().centerCrop().fit().into(today_btn_image)
            }
        })

        viewModel.liveState.observe(this, Observer {
            when (it?.status) {
                Status.ERROR -> {
                    Snackbar.make(this.view!!, "Error getting data!", Snackbar.LENGTH_SHORT).show()
                }
                Status.SUCCESSFUL -> {}
                Status.LOADING -> {}
            }
        })

        today_btn_image.setOnClickListener {
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(imageIntent, "Select image"), REQUEST_IMAGE_PICK)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        viewModel.setActivity(TimeUtils.getTodayLong())
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentDay.value?.let {
            saveDayActivity(it.imagePath) //todo too hacky
        }
    }

    fun saveDayActivity(uri: String) {
        val emoji = emojiAdapter.selected
        val date = TimeUtils.getTodayLong()
        val physActivies = physActivityAdapter.activityList
        val moodText = today_mood_input.text.toString()
        val imagePath = uri
        val newActivity = DayActivity(date, physActivies, moodText, imagePath, emoji)

        viewModel.saveActivity(newActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val uri = data!!.data
            val stringUri = uri!!.toString()
            saveDayActivity(stringUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.setActivity(date)
                } else {
                    // todo: hardcoded string
                    Toast.makeText(requireContext(), "Need media permission to show image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}