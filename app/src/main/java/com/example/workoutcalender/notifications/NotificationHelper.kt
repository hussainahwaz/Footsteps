package com.example.workoutcalender.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.workoutcalender.model.Tracker

object NotificationHelper {
    private const val CHANNEL_ID = "tracker_reminders"

    private fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Tracker reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Reminders to log your trackers"
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun show(context: Context, tracker: Tracker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return // Not granted -- nothing we can do from a background receiver.
        }

        ensureChannel(context)

        val message = when {
            tracker.isIntervalGoal -> "Time to log ${tracker.name}"
            tracker.isDailyGoal -> "Don't forget ${tracker.name} today"
            else -> "${tracker.name}: ${tracker.completedInWeek()}/${tracker.targetPerWeek} this week"
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val contentIntent = PendingIntent.getActivity(
            context,
            tracker.id.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // TODO: swap for a proper monochrome/alpha-only icon (Image Asset Studio -> Notification
        // Icons) -- android.R.drawable.ic_dialog_info is a system placeholder only.
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(tracker.name)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(tracker.id.hashCode(), notification)
    }
}