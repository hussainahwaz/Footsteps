package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.workoutcalender.model.CompletionMethod
import com.example.workoutcalender.model.Tracker

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTrackerScreen(
    tracker: Tracker,
    onBack: () -> Unit,
    onSave: (name: String, icon: String, color: Color, method: CompletionMethod, targetPerWeek: Int, includeInOverall: Boolean) -> Unit,
) {
    TrackerForm(
        screenTitle = "Edit Tracker",
        initialName = tracker.name,
        initialIcon = tracker.icon,
        initialColor = tracker.color,
        initialMethod = tracker.method,
        initialTargetPerWeek = tracker.targetPerWeek,
        initialIncludeInOverall = tracker.includeInOverall,
        submitLabel = "Save Changes",
        onBack = onBack,
        onSubmit = onSave,
    )
}