package com.example.whatisup.src.data.repository

import android.util.Log
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.model.DayActivity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "DayActivityRepository"

class DayActivityRepository(private val db: AppDatabase) {

    fun getDayActivities(date1: Long, date2: Long): Maybe<List<DayActivity>> {
        return db.getDayActivityDao().getDayActivitivities(date1, date2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getDayActivities(): Maybe<List<DayActivity>> {
        return db.getDayActivityDao().getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getDayActivity(date: Long): Maybe<DayActivity> {
        return db.getDayActivityDao().getDayActivity(date)
            .doOnEvent { t1, t2 ->
                if (t1 == null && t2 == null) {
                    Log.w(TAG, "Found null date in db, creating empty activity!")
                    insertDayActivity(DayActivity()).subscribe()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun insertDayActivity(activity: DayActivity): Completable {
        /*
        Rooms @Dao methods can't return Completable -> wrap the insert call in here with
        fromAction(() -> {})
         */
        return Completable.fromAction { db.getDayActivityDao().insert(activity) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }
}