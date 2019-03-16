package com.example.whatisup.src.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey val id: Long,
    val date: Long,
    val header: String,
    val text: String,
    val type: String)