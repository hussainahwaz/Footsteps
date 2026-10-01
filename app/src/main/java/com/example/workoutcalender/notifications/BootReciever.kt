package com.example.workoutcalender.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.workoutcalender.data.TrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Alarms don't survive a reboot -- this re-registers one per tracker that has a reminder set. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = TrackerRepository(context.applicationContext)
                repository.trackers.first().forEach { tracker ->
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