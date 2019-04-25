package com.example.whatisup.src.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.drawable.Drawable
import com.example.whatisup.R

fun getReminderText(type: String, context: Context): String{
    return when (type) {
        "still" -> { context.getString(R.string.reminder_still_text)}
        "goal_walking" -> { context.getString(R.string.reminder_goal_walking_text)}
        else -> {""}
    }
}

fun getReminderNotificationText(type: String, context: Context): String{
    return when (type) {
        "still" -> { context.getString(R.string.reminder_still_notification)}
        else -> {""}
    }
}

fun getReminderHeader(type: String, context: Context): String{
    return when (type) {
        "still" -> { context.getString(R.string.reminder_still_header)}
        "goal_walking" -> { context.getString(R.string.reminder_goal_walking_header)}
        else -> {""}
    }
}

fun getReminderDrawable(type: String, context: Context): Drawable{
    return when (type) {
        "still" -> { context.getDrawable(R.drawable.sitting)!! }
        "goal_walking" -> { context.getDrawable(R.drawable.reward)!! }
        else -> { context.getDrawable(R.drawable.sitting)!! }
    }
}

@Entity
data class Reminder(
    @PrimaryKey val id: Long,
    val date: Long,
    val header: String,
    val text: String,
    val type: String)