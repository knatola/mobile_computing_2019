package com.example.whatisup.src.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.whatisup.src.data.service.ActivityTrackingService
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

private const val TAG = "ActivityProvider"
private const val REQUEST_CODE = 10
const val TYPE_OTHER = 13

object ActivityProvider {

    private var currentActivity = DetectedActivity.STILL
    var lastTime: Long = System.currentTimeMillis()

    fun requestUpdates(context: Context) {
        Log.d(TAG, "Started Activity Tracking!")
        val intent = Intent(context, ActivityTrackingService::class.java)
        val pendingIntent = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val task = ActivityRecognition.getClient(context).requestActivityTransitionUpdates(createRequests(), pendingIntent)
        val task2 = ActivityRecognition.getClient(context).requestActivityUpdates(10000L, pendingIntent)

        task.addOnSuccessListener {
            Log.d(TAG, "Request for updates success")
        }

        task.addOnFailureListener {
            Log.w(TAG, "Error requesting activity updates", it)
        }

        task2.addOnSuccessListener {
            Log.d(TAG, "Request 2 for updates success")
        }

        task2.addOnFailureListener {
            Log.w(TAG, "Error in request 2", it)
        }
    }

    fun currentActivity() = currentActivity

    fun setCurrentActivity(type: Int) {
        this.currentActivity = type
    }

    private fun createRequests(): ActivityTransitionRequest {
        val transitions = mutableListOf<ActivityTransition>()

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        return ActivityTransitionRequest(transitions)
    }
}