package com.example.whatisup.src.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.utils.TimeUtils
import com.google.android.gms.location.DetectedActivity

// could have made this better
fun createEmptyActivities(): MutableList<PhysicalActivity> {
    val act1 = PhysicalActivity(DetectedActivity.STILL, 1)
    val act2 = PhysicalActivity(DetectedActivity.WALKING, 1)
    val act3 = PhysicalActivity(DetectedActivity.ON_FOOT, 1)
    val act4 = PhysicalActivity(DetectedActivity.ON_BICYCLE, 1)
    val act5 = PhysicalActivity(DetectedActivity.RUNNING, 1)
    val act6 = PhysicalActivity(TYPE_OTHER, 1)

    return mutableListOf(act1, act2, act3, act4, act5, act6)
}

@Entity
data class DayActivity(
    @PrimaryKey var date: Long = 0,
    var activities: MutableList<PhysicalActivity> = createEmptyActivities(),
    @ColumnInfo(name = "mood_text") var moodText: String = "",
    @ColumnInfo(name = "image_path") var imagePath: String = "",
    @ColumnInfo(name = "emoji_type") var emoji: Int = 0,
    var imageCaption: String = "",
    @Ignore var expanded: Boolean = false
) {
    constructor() : this(TimeUtils.getTodayLong(), createEmptyActivities(), "", "", 2, "", false)

    override fun toString(): String {
        return "date: $date, activities:$activities, moodText: $moodText, imagePath: $imagePath, emoji: $emoji, caption: $imageCaption"
    }
}