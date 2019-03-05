package com.example.whatisup.src.data.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.data.RxBus
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.Injection
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

private const val TAG = "ActivityTrackingService"

class ActivityTrackingService() : IntentService(TAG) {

    private val activityRepo = Injection.provideDayActivityRepo(this)

    @SuppressWarnings("CheckResult")
    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val currentActivity = ActivityProvider.currentActivity()

            val activity = result.mostProbableActivity

            Log.d(TAG, "onHandleIntent ${activity}")

            if (activity.type != currentActivity) {

                val time = System.currentTimeMillis()

                val duration = time - ActivityProvider.lastTime
                ActivityProvider.lastTime = time
                val type = currentActivity

                Log.d(TAG, "duration: $duration, type: $type")
                activityRepo.getDayActivity(TimeUtils.getTodayLong()).subscribe({
//                    val activities = it.activities + PhysicalActivity(type, duration)
                    val newActivity = update(it, PhysicalActivity(type, duration))
                    activityRepo.insertDayActivity(newActivity)
                        .subscribe({ Log.d(TAG, "updated DayActivity, ${newActivity.activities}") }, { e ->
                            Log.w(
                                TAG, "Error updating dayActivity!", e
                            )
                        })
                    RxBus.publish(newActivity)
                }, { e -> Log.w(TAG, "Error handling DayActivity!") })

                if (activity.type == DetectedActivity.STILL || activity.type == DetectedActivity.ON_BICYCLE
                    || activity.type == DetectedActivity.ON_FOOT || activity.type == DetectedActivity.RUNNING
                    || activity.type == DetectedActivity.WALKING) {
//                    Log.d(TAG, "Created new activity with type: $type, duration : $duration")
                    ActivityProvider.setCurrentActivity(activity.type)
                } else {
                    ActivityProvider.setCurrentActivity(TYPE_OTHER)
                }
            }
        }
    }

    private fun update(old: DayActivity, activity: PhysicalActivity): DayActivity {
            val oldAct = old.activities.find { it.type == activity.type }
            oldAct?.let {
                oldAct.duration = oldAct.duration + activity.duration
                return old
            }
        old.activities.add(activity)

        return old
    }
}