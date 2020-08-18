package com.example.whatisup.src.data.repository

import com.example.whatisup.src.data.dao.DayActivityDao
import com.example.whatisup.src.data.model.DayActivity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayActivityRepositoryImpl @Inject constructor(private val dao: DayActivityDao) :
    DayActivityRepository {

    override fun getDayActivities(date1: Long, date2: Long): Maybe<List<DayActivity>> {
        return dao.getDayActivitivities(date1, date2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getDayActivities(): Maybe<List<DayActivity>> {
        return dao.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getDayActivity(date: Long): Maybe<DayActivity> {
        return dao.getDayActivity(date)
            .doOnEvent { t1, t2 ->
                if (t1 == null && t2 == null) {
                    insertDayActivity(DayActivity()).subscribe()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun insertDayActivity(activity: DayActivity): Completable {
        /*
        Rooms @Dao methods can't return Completable -> wrap the insert call in here with
        fromAction(() -> {})
         */
        return Completable.fromAction { dao.insert(activity) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }
}