package com.example.whatisup.src.data.dao

import android.arch.persistence.room.*
import com.example.whatisup.src.data.model.DayActivity
import io.reactivex.Single

@Dao
interface DayActivityDao {

    @Query("SELECT * FROM dayactivity")
    fun getAll(): Single<List<DayActivity>>

    @Query("SELECT * FROM dayactivity WHERE date = :date")
    fun getDayActivity(date: Long): Single<DayActivity>

    @Query("SELECT * FROM dayactivity WHERE date > :date1 AND date <= :date2")
    fun getDayActivitivities(date1: Long, date2: Long): Single<List<DayActivity>>

    @Insert
    fun insertAll(vararg dayActivities: DayActivity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dayActivity: DayActivity)

    @Update
    fun update(vararg activities: DayActivity)
}