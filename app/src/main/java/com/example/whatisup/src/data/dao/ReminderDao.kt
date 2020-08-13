package com.example.whatisup.src.data.dao

import androidx.room.*
import com.example.whatisup.src.data.model.Reminder
import io.reactivex.Maybe

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder")
    fun getAll(): Maybe<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}