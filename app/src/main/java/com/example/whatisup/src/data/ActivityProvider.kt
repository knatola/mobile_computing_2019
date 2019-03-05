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
        val pendingIntent = PendingIntent.getService(context, REQUEST_CODE, intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val task = ActivityRecognition.getClient(context).requestActivityUpdates(10000, pendingIntent)

        task.addOnSuccessListener {
            Log.d(TAG, "Successfully found new activity")
        }

        task.addOnFailureListener {
            Log.w(TAG, "Error in handling new activity")
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
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        return ActivityTransitionRequest(transitions)
    }
}