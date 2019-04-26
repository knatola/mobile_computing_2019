package com.example.whatisup.src.data.repository

import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.utils.createMocks
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Mock Implementation of [DayActivityRepository], don't want to use an actual DB in test automation.
 */
class MockDayActivityRepository : DayActivityRepository {

    override fun getDayActivities(date1: Long, date2: Long): Maybe<List<DayActivity>> {
        return Maybe.create<List<DayActivity>> { createMocks() } // return the same list of activities as is mocked in db
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getDayActivity(date: Long): Maybe<DayActivity> {
        return Maybe.create<DayActivity> { DayActivity(date = date) } // just an empty Activity
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun insertDayActivity(activity: DayActivity): Completable {
        return Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }
}