package com.example.whatisup.src.ui

import android.app.Activity.RESULT_OK
import androidx.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.whatisup.R
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.ui.adapter.EmojiAdapter
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.src.utils.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.day_view_fragment_layout.*
import kotlinx.android.synthetic.main.emoji_info_layout.*
import kotlinx.android.synthetic.main.graph_info_layout.*
import kotlinx.android.synthetic.main.image_info_layout.*
import kotlinx.android.synthetic.main.mood_info_layout.*

private const val TAG = "DayFragment"
private const val REQUEST_IMAGE_PICK = 11

@AndroidEntryPoint
class DayFragment: Fragment() {

    private val viewModel: DayActivityViewModel by viewModels()
    private lateinit var emojiAdapter: EmojiAdapter

    private var date: Long = 0

    // todo: move to viewmodel state
    // booleans to track the different expanded states, simple
    private var imageExpanded = true
    private var emojiExpanded = true
    private var activityExpanded = true
    private var moodExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            date = requireArguments().getLong("date")
            viewModel.setActivity(date)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.day_view_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emoji_recycler.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )
        emojiAdapter = EmojiAdapter(getEmojiList(), requireContext())
        emojiAdapter.setVm(viewModel)
        emoji_recycler.adapter = emojiAdapter

        activity_graph.setDefaultTargetPoints() // default targets

        viewModel.state().observe(viewLifecycleOwner, Observer {
            it?.let { state ->
                emoji_info_header.text = getEmojiText(state.selectedEmoji, requireContext())
                emoji_header2.text = getEmojiText(state.selectedEmoji, requireContext())
                (requireActivity() as MainActivity).setTitle(stringDate(state.currentDay.date))

                if (state.currentDay.imageCaption == "") image_header2.text = getString(R.string.no_caption)
                else image_header2.text = state.currentDay.imageCaption

                image_caption_input.text = Editable.Factory.getInstance().newEditable(state.currentDay.imageCaption)
                today_mood_input.setText(state.currentDay.moodText)
                mood_header2.text = state.currentDay.moodText.take(10) + "..." // hardcoded
                activity_amount.text = totalActivityText(10, requireContext()) //todo: check this
                activity_header2.text = totalActivityText(10, requireContext())
                activity_graph.setData(state.currentDay.activities)

                if (state.currentDay.imagePath != "")
                    Picasso.get().load(state.currentDay.imagePath).noPlaceholder().centerCrop().fit().into(today_btn_image)
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

            if (imageExpanded) {
                image_header2.visibility = View.INVISIBLE
            } else {
                image_header2.visibility = View.VISIBLE
                hideKeyboard(requireActivity(), it)
            }
        }

        edit_targets.setOnClickListener {
            activity_graph.allowTargetMovement = !activity_graph.allowTargetMovement

            if (activity_graph.allowTargetMovement) {
                edit_targets.text = requireContext().getString(R.string.day_edit_targets_lock)
                edit_targets.setTextColor(requireContext().getColor(R.color.red))
            } else {
                edit_targets.text = requireContext().getString(R.string.day_edit_targets)
                edit_targets.setTextColor(requireContext().getColor(R.color.black))
            }
        }

        emoji_edit_view.setOnClickListener {
            emojiExpanded = setExpanded(emojiExpanded, emoji_expanded_view, emoji_expand)

            if (emojiExpanded) emoji_header2.visibility = View.INVISIBLE else emoji_header2.visibility = View.VISIBLE
        }

        activity_edit_view.setOnClickListener {
            activityExpanded = setExpanded(activityExpanded, activity_expanded_view, activity_expand)

            if (activityExpanded) activity_header2.visibility = View.INVISIBLE else activity_header2.visibility = View.VISIBLE
        }

        mood_edit_view.setOnClickListener {
            moodExpanded = setExpanded(moodExpanded, mood_expanded_view, mood_expand)

            if (moodExpanded) {
                mood_header2.visibility = View.INVISIBLE
            } else {
                mood_header2.visibility = View.VISIBLE
                hideKeyboard(requireActivity(), it)
            }
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
        viewModel.liveState.value?.let {
            saveDayActivity(it.currentDay.imagePath) //todo too hacky
        }
    }

    private fun saveDayActivity(uri: String) {
        val emoji = emojiAdapter.selected
        val date = TimeUtils.getTodayLong()
        val physActivies = viewModel.getState().currentDay.activities
        val moodText = today_mood_input.text.toString()
        val imagePath = uri
        val caption = image_caption_input.text.toString()
        val newActivity = DayActivity(date, physActivies, moodText, imagePath, emoji, caption)

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