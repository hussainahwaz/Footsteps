package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.workoutcalender.model.CompletionMethod

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTrackerScreen(
    onBack: () -> Unit,
    onCreate: (name: String, icon: String, color: Color, method: CompletionMethod, targetPerWeek: Int, intervalDays: Int?, includeInOverall: Boolean, reminderHour: Int?, reminderMinute: Int?) -> Unit,
) {
    TrackerForm(
        screenTitle = "New Tracker",
        initialName = "",
        initialIcon = "custom",
        initialColor = TRACKER_FORM_PALETTE.first(),
        initialMethod = CompletionMethod.QUICK,
        initialTargetPerWeek = 7,
        initialIntervalDays = null,
        initialIncludeInOverall = true,
        initialReminderHour = null,
        initialReminderMinute = null,
        submitLabel = "Create Tracker",
        onBack = onBack,
        onSubmit = onCreate,
    )
}