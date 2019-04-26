package com.example.whatisup.src.utils

import android.net.Uri
import com.example.whatisup.R
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.data.model.Reminder
import com.google.android.gms.location.DetectedActivity
import java.sql.Time
import java.util.*

fun getURLForResource(resourceId: Int): String {
    return Uri.parse("android.resource://" + com.example.whatisup.R::class.java.getPackage()!!.name + "/" + resourceId).toString()
}

fun createMockReminders(): List<Reminder> {
    val reminder1= Reminder(0, TimeUtils.getTodayLong(), "TestHeader1", "test_reminder1", "still")
    val reminder2 = Reminder(1, TimeUtils.getTodayLong(), "TestHeader2", "test_reminder2", "goal_walking")
    val reminder3 = Reminder(2, TimeUtils.getTodayLong(), "TestHeader3", "test_reminder3", "still")
    val reminder4 = Reminder(3, TimeUtils.getTodayLong(), "TestHeader4", "test_reminder4", "goal_walking")
    val reminder5 = Reminder(4, TimeUtils.getTodayLong(), "TestHeader5", "test_reminder5", "still")
    val reminder6 = Reminder(5, TimeUtils.getTodayLong(), "TestHeader6", "test_reminder6", "still")
    val reminder7 = Reminder(6, TimeUtils.getTodayLong(), "TestHeader7", "test_reminder7", "still")
    val reminder8 = Reminder(7, TimeUtils.getTodayLong(), "TestHeader8", "test_reminder8", "goal_walking")

    return listOf(reminder1, reminder2, reminder3, reminder4, reminder5, reminder6, reminder7, reminder8)
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
    returnList.add(DayActivity(date = 9999L))

    return returnList.toList()
}