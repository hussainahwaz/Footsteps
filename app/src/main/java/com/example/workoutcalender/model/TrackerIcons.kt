package com.example.workoutcalender.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Tracker.icon is a plain String key (e.g. "workout"), not an emoji glyph -- this
 * keeps the field simple to serialize/store (see TrackerRepository) while letting
 * the UI render a real, monochrome, tinted vector icon instead. Falls back to a
 * star for any unrecognized key, e.g. old data or a key that gets removed later.
 */
private val ICONS: Map<String, ImageVector> = mapOf(
    "workout" to Icons.Filled.FitnessCenter,
    "reading" to Icons.Filled.MenuBook,
    "swimming" to Icons.Filled.Pool,
    "walking" to Icons.Filled.DirectionsWalk,
    "running" to Icons.Filled.DirectionsRun,
    "studying" to Icons.Filled.School,
    "meditation" to Icons.Filled.SelfImprovement,
    "water" to Icons.Filled.WaterDrop,
    "journaling" to Icons.Filled.EditNote,
    "sleep" to Icons.Filled.Bedtime,
    "gratitude" to Icons.Filled.Favorite,
    "language" to Icons.Filled.Translate,
    "creative" to Icons.Filled.Palette,
    "tidying" to Icons.Filled.CleaningServices,
    "custom" to Icons.Filled.Star, // default for a tracker not built from a preset
)

fun iconFor(key: String): ImageVector = ICONS[key] ?: Icons.Filled.Star

/** Ordered list for icon pickers, e.g. on the Create Tracker screen. */
val trackerIconOptions: List<Pair<String, ImageVector>> = ICONS.toList()
