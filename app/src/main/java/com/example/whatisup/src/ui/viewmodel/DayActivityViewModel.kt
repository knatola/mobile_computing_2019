package com.example.whatisup.src.ui.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.data.RxBus
import com.example.whatisup.src.data.common.State
import com.example.whatisup.src.data.common.Status
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.utils.TimeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "DayActivityViewModel"

class DayActivityViewModel(private val dayActivityRepository: DayActivityRepository) : BaseViewModel() {

    private var current = 1
    var count = 0
    var dayActivities = MutableLiveData<List<DayActivity>>()
    val currentDay = MutableLiveData<DayActivity>()
    val selectedEmoji = MutableLiveData<Int>()

    init {
        setActivities()
        setActivity(TimeUtils.getTodayLong())
        val disposable = RxBus.listen(DayActivity::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                currentDay.value = it
            }, { e ->
                Log.w(TAG, "error!", e)
            })
    }

    private fun getDates(): List<Long> {
        count++
        return TimeUtils.getWeekAgo(count)
    }

    fun setEmoji(emoji: Int) {
        selectedEmoji.value = emoji
    }

    fun setActivity(date: Long) {
        setState(State(Status.LOADING))
        val disposable = dayActivityRepository.getDayActivity(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "Current day = ${it.date}, ${it.imagePath}")
                currentDay.value = it
                selectedEmoji.value = it.emoji
                setState(State(Status.SUCCESSFUL))
            }, { e ->
                Log.w(TAG, e)
                setState(State(Status.ERROR, e))
            })
        addDisposable(disposable)
    }

    @SuppressLint("CheckResult")
    fun saveActivity(act: DayActivity) {
        dayActivityRepository.insertDayActivity(act).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.i(TAG, "Saved DayActivity successfully")}, { e -> Log.w(TAG, "failed to save DayActivity")})
    }

    fun setActivities(forward: Boolean){

        val dates: List<Long>

        if (forward && current > 0)  {
            dates = TimeUtils.getWeekAgo(current - 1)
            current--
        } else if (!forward){
            dates = TimeUtils.getWeekAgo(current + 1)
            current++
        } else {
            dates = TimeUtils.getWeekAgo(current)
        }
        setActivities(dates[0], dates[1])
    }

    fun getData() = dayActivities

    private fun setActivities(date1: Long = getDates()[0], date2: Long = TimeUtils.getTodayLong()) {
        setState(State(Status.LOADING))
        val disposable = dayActivityRepository.getDayActivities(date1, date2)
            .subscribe({ it ->
                dayActivities.value = it
                setState(State(Status.SUCCESSFUL))
            }, { e ->
                Log.w(TAG, "Error getting activities!", e)
                setState(State(Status.ERROR, e))
            })
        addDisposable(disposable)
    }
}