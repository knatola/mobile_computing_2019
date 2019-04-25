package com.example.whatisup.src.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.whatisup.R
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.Emoji
import com.google.android.gms.location.DetectedActivity

const val REQUEST_PERMISSION_CODE = 21

private val activityTypes = arrayOf("Walking", "Running", "Gym", "Swimming", "Cycling")
private val emojiList = arrayListOf(
    Emoji(false, 0),
    Emoji(false, 1),
    Emoji(false, 2),
    Emoji(false, 3),
    Emoji(false, 4)
    )

fun totalActivityText(activity: Long, context: Context): String {
    return context.getString(R.string.activity_high )
}

fun getEmojiText(emoji: Int, context: Context): String {
    return when (emoji) {
        0 -> context.getString(R.string.emoji_very_sad)
        1 -> context.getString(R.string.emoji_sad)
        2 -> context.getString(R.string.emoji_neutral)
        3 -> context.getString(R.string.emoji_happy)
        4 -> context.getString(R.string.emoji_very_happy)
        else -> context.getString(R.string.emoji_neutral)
    }
}

fun getActivityText(type: Int, context: Context): String {
    return when (type) {
        DetectedActivity.ON_FOOT -> context.getString(R.string.on_foot)
        DetectedActivity.RUNNING -> context.getString(R.string.running)
        DetectedActivity.STILL -> context.getString(R.string.still)
        TYPE_OTHER-> context.getString(R.string.other)
        DetectedActivity.ON_BICYCLE -> context.getString(R.string.on_bicycle)
        DetectedActivity.WALKING -> context.getString(R.string.walking)
        else -> context.getString(R.string.other)
    }
}

fun checkType(type: Int): Boolean {
    if (type == DetectedActivity.ON_FOOT) return true
    if (type == DetectedActivity.RUNNING) return true
    if (type == DetectedActivity.STILL) return true
    if (type == DetectedActivity.ON_BICYCLE) return true
    if (type == DetectedActivity.WALKING) return true
    if (type == TYPE_OTHER) return true

    return false
}

fun getEmojiList() = emojiList

fun getActivityIcon(type: Int, context: Context): Drawable? {
    return when (type) {
        DetectedActivity.ON_FOOT -> context.getDrawable(R.drawable.activity_walking)
        DetectedActivity.WALKING -> context.getDrawable(R.drawable.activity_walking)
        DetectedActivity.RUNNING -> context.getDrawable(R.drawable.activity_running)
        DetectedActivity.STILL -> context.getDrawable(R.drawable.activity_gym)
        TYPE_OTHER -> context.getDrawable(R.drawable.activity_swimming)
        DetectedActivity.ON_BICYCLE -> context.getDrawable(R.drawable.activity_cycling)
        else -> context.getDrawable(R.drawable.activity_running)
    }
}

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getEmojiDrawable(type: Int, context: Context): Drawable? {
    return when (type) {
        0 -> context.getDrawable(R.drawable.unhappy)
        1 -> context.getDrawable(R.drawable.sad)
        2 -> context.getDrawable(R.drawable.confused)
        3 -> context.getDrawable(R.drawable.happy)
        4 -> context.getDrawable(R.drawable.smiling)
        else -> context.getDrawable(R.drawable.sad)
    }
}

fun stringDate(date: Long): String{
    val strings = date.toString().chunked(2)
    return "${strings[2]}.${strings[1]}.${strings[0]}"
}