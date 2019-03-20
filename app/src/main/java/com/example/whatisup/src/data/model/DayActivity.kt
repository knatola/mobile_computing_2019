package com.example.whatisup.src.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class DayActivity(
    @PrimaryKey var date: Long = 0,
    var activities: MutableList<PhysicalActivity> = mutableListOf(),
    @ColumnInfo(name = "mood_text") var moodText: String = "",
    @ColumnInfo(name = "image_path") var imagePath: String = "",
    @ColumnInfo(name = "emoji_type") var emoji: Int = 0,
    var imageCaption: String = "",
    @Ignore var expanded: Boolean = false
) {
    constructor() : this(0L, mutableListOf<PhysicalActivity>(), "", "", 2, "", false)

    override fun toString(): String {
        return "date: $date, activities:$activities, moodText: $moodText, imagePath: $imagePath, emoji: $emoji, caption: $imageCaption"
    }
}