package com.example.whatisup.src.data.repository

import com.example.whatisup.src.data.model.DayActivity
import io.reactivex.Completable
import io.reactivex.Maybe

/**
 * Dependency inversion interface for DayActivityRepositories
 *
 */
interface DayActivityRepository {

    fun getDayActivities(date1: Long, date2: Long): Maybe<List<DayActivity>>

    fun getDayActivity(date: Long): Maybe<DayActivity>

    fun insertDayActivity(activity: DayActivity): Completable
}