package com.example.workoutcalender.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.ZonedDateTime

/**
 * One exact alarm per tracker, always for "the next occurrence of hour:minute".
 * Each alarm reschedules itself for the following day when it fires (see
 * ReminderReceiver) -- there's no single repeating alarm because whether to
 * actually notify depends on that day's completion state, which can only be
 * checked at fire time.
 */
object AlarmScheduler {
    const val EXTRA_TRACKER_ID = "tracker_id"

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context, trackerId: String, hour: Int, minute: Int) {
        if (!canScheduleExactAlarms(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, trackerId)

        val now = ZonedDateTime.now()
        var trigger = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!trigger.isAfter(now)) trigger = trigger.plusDays(1)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            trigger.toInstant().toEpochMilli(),
            pendingIntent,
        )
    }

    fun cancel(context: Context, trackerId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, trackerId))
    }

    private fun buildPendingIntent(context: Context, trackerId: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TRACKER_ID, trackerId)
        }
        return PendingIntent.getBroadcast(
            context,
            trackerId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

