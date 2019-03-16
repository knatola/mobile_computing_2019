package com.example.whatisup.src.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class DayActivity(
    @PrimaryKey val date: Long,
    val activities: MutableList<PhysicalActivity>,
    @ColumnInfo(name = "mood_text") val moodText: String,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "emoji_type") val emoji: Int,
    var imageCaption: String = ""
) {
    @Ignore
    constructor() : this(0L, mutableListOf<PhysicalActivity>(), "", "", 2)

    override fun toString(): String {
        return "date: $date, activities:$activities, moodText: $moodText, imagePath: $imagePath, emoji: $emoji, caption: $imageCaption"
    }
}