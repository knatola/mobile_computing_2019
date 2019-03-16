package com.example.whatisup.src.utils

import android.net.Uri
import com.example.whatisup.R
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.data.model.Reminder
import com.google.android.gms.location.DetectedActivity
import java.util.*

fun getURLForResource(resourceId: Int): String {
    return Uri.parse("android.resource://" + com.example.whatisup.R::class.java.getPackage()!!.name + "/" + resourceId).toString()
}

fun createMockReminders(): List<Reminder> {
    val reminder1= Reminder(0, 0, "TestHeader1", "test_reminder1", "random")
    val reminder2 = Reminder(1, 0, "TestHeader2", "test_reminder2", "random")
    val reminder3 = Reminder(2, 0, "TestHeader3", "test_reminder3", "random")
    val reminder4 = Reminder(3, 0, "TestHeader4", "test_reminder4", "random")
    val reminder5 = Reminder(4, 0, "TestHeader5", "test_reminder5", "random")

    return listOf(reminder1, reminder2, reminder3, reminder4, reminder5)
}

fun createMocks(): List<DayActivity> {
    val dates = TimeUtils.getWeekAgo(2)
    var farEnd = dates[0]
    val today = TimeUtils.getTodayLong()

    val returnList = mutableListOf<DayActivity>()

    while (farEnd <= today) {

        val physActivity1 = PhysicalActivity(DetectedActivity.STILL, kotlin.random.Random.nextLong(10000000, 24000000))
        val physActivity2 = PhysicalActivity(DetectedActivity.WALKING, kotlin.random.Random.nextLong(10000000, 24000000))
        val physActivity3 = PhysicalActivity(DetectedActivity.ON_FOOT, kotlin.random.Random.nextLong(10000000, 24000000))
        val physActivity4 = PhysicalActivity(DetectedActivity.ON_BICYCLE, kotlin.random.Random.nextLong(10000000, 24000000))
        val physActivity5 = PhysicalActivity(DetectedActivity.RUNNING, kotlin.random.Random.nextLong(10000000, 24000000))
        val physActivity6 = PhysicalActivity(TYPE_OTHER, kotlin.random.Random.nextLong(10000000, 24000000))
//        val physActivity2 = PhysicalActivity(1, 10000L)
        val physActList = mutableListOf(physActivity1, physActivity2,
            physActivity3, physActivity4,
            physActivity5, physActivity6)
        val moodText = "test_mood:$farEnd"
        val path = Uri.parse("android.resource://com.example.whatisup/drawable/act_background.png")
        val imagePath = getURLForResource(R.drawable.act_background)
        val emoji = Random().nextInt(4)
        val mockActivity = DayActivity(farEnd, physActList, moodText, imagePath, emoji)

        returnList.add(mockActivity)
        farEnd++
    }

    return returnList.toList()
}