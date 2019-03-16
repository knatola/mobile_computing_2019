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
import android.widget.ImageView
import android.widget.Toast
import com.example.whatisup.R
import com.example.whatisup.src.data.common.Status
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.ui.adapter.ActivityAdapter
import com.example.whatisup.src.ui.adapter.EmojiAdapter
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory
import com.example.whatisup.src.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.day_view_fragment_layout.*
import java.lang.IllegalStateException
import kotlin.math.exp

private const val TAG = "DayFragment"
private const val REQUEST_IMAGE_PICK = 11

class DayFragment: Fragment() {

    private lateinit var viewModel: DayActivityViewModel
    private lateinit var viewModelFactory: DayActivityViewModelFactory
    private lateinit var emojiAdapter: EmojiAdapter
    private lateinit var physActivityAdapter: ActivityAdapter

    private var date: Long = 0

    // booleans to track the different expanded states, simple
    private var imageExpanded = true
    private var emojiExpanded = true
    private var activityExpanded = true
    private var moodExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = Injection.provideDayActivityVmFactory(requireContext())
        viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(DayActivityViewModel::class.java)
        } ?: throw IllegalStateException("Invalid Activity!")

        arguments?.let {
            date = arguments!!.getLong("date")
            viewModel.setActivity(date)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.day_view_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emoji_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        physActivityAdapter = ActivityAdapter(mutableListOf(), requireContext())
        emojiAdapter = EmojiAdapter(getEmojiList(), requireContext())
        emoji_recycler.adapter = emojiAdapter

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

        image_edit_view.setOnClickListener {
            imageExpanded = setExpanded(imageExpanded, image_expanded_view, image_expand)
        }

        emoji_edit_view.setOnClickListener {
            emojiExpanded = setExpanded(emojiExpanded, emoji_expanded_view, emoji_expand)
        }

        activity_edit_view.setOnClickListener {
            activityExpanded = setExpanded(activityExpanded, activity_expanded_view, activity_expand)
        }

        mood_edit_view.setOnClickListener {
            moodExpanded = setExpanded(moodExpanded, mood_expanded_view, mood_expand)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setExpanded(expanded: Boolean, view: View, imageV: ImageView): Boolean {
        when (expanded) {
            true -> {
                view.visibility = View.GONE
                imageV.setImageDrawable(requireContext().getDrawable(R.drawable.ic_expand_more_black_24dp))
            }
            false -> {
                view.visibility = View.VISIBLE
                imageV.setImageDrawable(requireContext().getDrawable(R.drawable.ic_expand_less_black_24dp))
            }
        }

        return !expanded
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