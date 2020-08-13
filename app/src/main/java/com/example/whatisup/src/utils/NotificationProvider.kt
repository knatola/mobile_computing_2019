package com.example.whatisup.src.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.whatisup.R
import com.example.whatisup.src.ui.MainActivity

private const val CHANNEL_REMINDER_ID = "reminder"

class NotificationProvider(context: Context) {

    private val channels = arrayOf(CHANNEL_REMINDER_ID)

    init {
        createNotificationChannels(context)
    }

    fun getNotification(context: Context, type: String): Notification {
        val channel = CHANNEL_REMINDER_ID

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingInt = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationBuilder = NotificationCompat.Builder(context, channel)
        val notificationStyle = NotificationCompat.BigTextStyle()
        notificationStyle.setBigContentTitle(getContentTitle(type, context))
        notificationStyle.bigText(getBigText(type, context))
        notificationStyle.setSummaryText(getsummaryText(type, context))
        notificationBuilder.setStyle(notificationStyle)

        notificationBuilder.setWhen(System.currentTimeMillis())
        notificationBuilder.setSmallIcon(R.drawable.sitting)
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.sitting))
        notificationBuilder.priority = Notification.PRIORITY_DEFAULT // non-deprecated NotificationManager priority needs higher min api, maybe will change this at some point
        notificationBuilder.setCategory(Notification.CATEGORY_SERVICE)
        notificationBuilder.setContentIntent(pendingInt)

        val notification = notificationBuilder.build()
        with(NotificationManagerCompat.from(context)) {
            notify(9, notification) // in theory there shouldn't be 2 notifications at the same time so hardcoded int goes here
        }

        return notification
    }

    private fun getsummaryText(type: String, context: Context): String {
        return when (type) {
            "still" -> {
                context.getString(R.string.reminder_still_notification)
            }
            else -> { "" }
        }
    }

    private fun getContentTitle(type: String, context: Context): String {
        return when (type) {
            "still" -> {
                context.getString(R.string.reminder_still_header)
            }
            else -> { "" }
        }
    }

    private fun getBigText(type: String, context: Context): String {
        return when (type) {
            "still" -> {
                context.getString(R.string.reminder_still_notification)
            }
            else -> { "" }
        }
    }

    private fun createNotificationChannels(context: Context) {

        channels.forEach {
            when (it) {
                CHANNEL_REMINDER_ID -> {
                    createChannel(context.getString(R.string.reminder_notification_channel_name),
                        it,
                        context.getString(R.string.reminder_notification_description),
                        context)
                }
            }
        }
    }

    private fun createChannel(name: String, id: String, desc: String, context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = desc
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
}