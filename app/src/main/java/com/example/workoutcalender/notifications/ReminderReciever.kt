package com.example.workoutcalender.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.workoutcalender.data.TrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Fires once per tracker per day. Reads the tracker fresh from DataStore (not from
 * any in-memory app state, since the app may not be running), decides whether to
 * actually notify, then reschedules itself for the same time tomorrow regardless --
 * the reminder stays "on" until the tracker is deleted or the reminder is turned off.
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val trackerId = intent.getStringExtra(AlarmScheduler.EXTRA_TRACKER_ID) ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = TrackerRepository(context.applicationContext)
                val tracker = repository.trackers.first().find { it.id == trackerId }

                if (tracker != null) {
                    if (tracker.needsReminderToday()) {
                        NotificationHelper.show(context, tracker)
                    }
                    tracker.reminderTime?.let { time ->
                        AlarmScheduler.schedule(context.applicationContext, tracker.id, time.hour, time.minute)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}