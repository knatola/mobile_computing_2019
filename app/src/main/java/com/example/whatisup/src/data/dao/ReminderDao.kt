package com.example.whatisup.src.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.whatisup.src.data.model.Reminder
import io.reactivex.Single

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder")
    fun getAll(): Single<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder)
}