package com.example.whatisup.src.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import com.example.whatisup.src.data.RxBus
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.utils.TimeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "DayActivityViewModel"

data class DayViewState(val activities: List<DayActivity> = listOf(),
                        val currentDay: DayActivity = DayActivity(),
                        val selectedEmoji: Int = 2)

class DayActivityViewModel @ViewModelInject constructor(private val dayActivityRepository: DayActivityRepository)
    : BaseViewModel<DayViewState>() {

    private var current = 1
    var count = 0

    override val liveState = MutableLiveData<DayViewState>()

    init {
        setState(DayViewState()) // init with "empty state"
        setActivities()
        setActivity(TimeUtils.getTodayLong())
        addDisposable(RxBus.listen(DayActivity::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                Log.d(TAG, "New value from ActivityService!")
                setState(this.getState().copy(currentDay = it, selectedEmoji = it.emoji))
            }, { e ->
                Log.w(TAG, "error!", e)
            })
        )
    }

    private fun getDates(): List<Long> {
        count++
        return TimeUtils.getWeekAgo(count)
    }

    fun setEmoji(emoji: Int) {
        setState(this.getState().copy(selectedEmoji = emoji))
    }

    fun setActivity(date: Long) {
        val disposable = dayActivityRepository.getDayActivity(date)
            .subscribe({
                Log.d(TAG, "Current day = ${it.date}, ${it.imagePath}")
                val state = this.getState().copy(currentDay = it, selectedEmoji = it.emoji)
                setState(state)
            }, { e ->
                Log.w(TAG, "Problem setting activity", e)
            })
        addDisposable(disposable)
    }

    @SuppressLint("CheckResult")
    fun saveActivity(act: DayActivity) {
        dayActivityRepository.insertDayActivity(act)
            .doOnComplete { setState(getState().copy(currentDay = act, selectedEmoji = act.emoji)) }
            .subscribe({ Log.i(TAG, "Saved DayActivity successfully")}, { e -> Log.w(TAG, "failed to save DayActivity", e)})
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

    private fun setActivities(date1: Long = getDates()[0], date2: Long = TimeUtils.getTodayLong()) {
        val disposable = dayActivityRepository.getDayActivities(date1, date2)
            .subscribe({
                val state = this.getState().copy(activities = it)
                setState(state)
            }, { e ->
                Log.w(TAG, "Error getting activities!", e)
            })
        addDisposable(disposable)
    }
}