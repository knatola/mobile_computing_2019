package com.example.whatisup.src.data

import com.example.whatisup.src.data.model.DayActivity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DayActivityRepository(private val db: AppDatabase) {

    fun getDayActivities(date1: Long, date2: Long): Single<List<DayActivity>> {
        return db.getDayActivityDao().getDayActivitivities(date1, date2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getDayActivities(): Single<List<DayActivity>> {
        return db.getDayActivityDao().getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getDayActivity(date: Long): Single<DayActivity> {
        return db.getDayActivityDao().getDayActivity(date)
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