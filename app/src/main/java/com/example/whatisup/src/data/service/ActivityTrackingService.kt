package com.example.whatisup.src.data.service

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import com.example.whatisup.R
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.data.RxBus
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.ui.MainActivity
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.Injection
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

private const val TAG = "ActivityTrackingService"
const val ACTIVITY_CHANNEL = "activity"

class ActivityTrackingService() : IntentService(TAG) {

    private val activityRepo = Injection.provideDayActivityRepo()

    var startTime: Long = System.currentTimeMillis()
    var currentType: Int = 0

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
//                    ActivityProvider.setCurrentActivity(TYPE_OTHER)
                }
            }
        }

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            Log.d(TAG, "handling result: $result")
            result?.let {
                for (event in it.transitionEvents) {
                    if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        startTime = event.elapsedRealTimeNanos
                        currentType = event.activityType
                    } else if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        val total = event.elapsedRealTimeNanos - startTime
                        Log.d(TAG, "updating: $currentType, $total")
                        activityRepo.getDayActivity(TimeUtils.getTodayLong()).subscribe({
                            //                    val activities = it.activities + PhysicalActivity(type, duration)
                            val newActivity = update(it, PhysicalActivity(currentType, total))
                            activityRepo.insertDayActivity(newActivity)
                                .subscribe({ Log.d(TAG, "updated DayActivity, ${newActivity.activities}") }, { e ->
                                    Log.w(
                                        TAG, "Error updating dayActivity!", e
                                    )
                                })
                            RxBus.publish(newActivity)
                        }, { e -> Log.w(TAG, "Error handling DayActivity!") })

                    }
                }
            }
        }
    }
    private fun createStillReminder() {
        Log.d(TAG, "Creating still reminder")

        createChannel("Activity reminders", "activity remidners", "Channel for activity related reminders.")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, ACTIVITY_CHANNEL)
            .setSmallIcon(R.drawable.activity_walking)
            .setContentTitle("You have been still for 15 minutes!")
            .setContentText("Maybe it is time to move!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }
    }

    private fun createChannel(name: String, id: String, desc: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = desc
            }
            (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    private fun update(old: DayActivity, activity: PhysicalActivity): DayActivity {
            val oldAct = old.activities.find { it.type == activity.type }

            oldAct?.let {
                oldAct.duration = oldAct.duration + activity.duration
                return old
            }

//        old.activities.add(activity)

        return old
    }
}