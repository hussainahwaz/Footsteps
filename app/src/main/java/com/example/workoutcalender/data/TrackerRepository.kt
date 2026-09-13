package com.example.workoutcalender.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.workoutcalender.model.CompletionMethod
import com.example.workoutcalender.model.EntryItem
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.TrackerEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.trackerDataStore by preferencesDataStore(name = "tracker_prefs")

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

// ---- Plain-data mirrors of the domain model -------------------------------
// Tracker itself isn't @Serializable (it uses Compose's Color and java.time.LocalDate,
// neither of which kotlinx.serialization knows how to handle out of the box). Rather
// than writing custom serializers for those two types, these DTOs store the same data
// in primitive form (color as an Int ARGB, dates as ISO strings) and get mapped back
// to/from the real model just below.

@Serializable
private data class EntryItemDto(val name: String, val value: String)

@Serializable
private data class TrackerEntryDto(
    val items: List<EntryItemDto> = emptyList(),
    val notes: String = "",
)

@Serializable
private data class TrackerDto(
    val id: String,
    val name: String,
    val icon: String,
    val colorArgb: Int,
    val method: String, // CompletionMethod.name
    val targetPerWeek: Int = 7, // defaults to 7 (daily) so old saved JSON without this key still decodes fine
    val completedDates: List<String> = emptyList(), // LocalDate.toString(), ISO-8601
    val entries: Map<String, TrackerEntryDto> = emptyMap(),
)

private fun Tracker.toDto(): TrackerDto = TrackerDto(
    id = id,
    name = name,
    icon = icon,
    colorArgb = color.toArgb(),
    method = method.name,
    targetPerWeek = targetPerWeek,
    completedDates = completedDates.map { it.toString() },
    entries = entries.mapKeys { it.key.toString() }.mapValues { (_, entry) ->
        TrackerEntryDto(
            items = entry.items.map { EntryItemDto(it.name, it.value) },
            notes = entry.notes,
        )
    },
)

private fun TrackerDto.toDomain(): Tracker = Tracker(
    id = id,
    name = name,
    icon = icon,
    color = Color(colorArgb),
    method = CompletionMethod.valueOf(method),
    targetPerWeek = targetPerWeek,
    completedDates = completedDates.map { LocalDate.parse(it) }.toSet(),
    entries = entries.mapKeys { LocalDate.parse(it.key) }.mapValues { (_, dto) ->
        TrackerEntry(
            items = dto.items.map { EntryItem(it.name, it.value) },
            notes = dto.notes,
        )
    },
)

/** Persists the user's trackers (and everything logged against them) across app restarts. */
class TrackerRepository(private val context: Context) {

    private val trackersKey = stringPreferencesKey("trackers_json")

    /**
     * Empty JSON key (nothing saved yet, i.e. a genuinely fresh install) starts the
     * user off with a few QUICK-tap presets instead of a blank screen. Once anything
     * is ever saved -- including the user deleting every tracker, which saves an
     * empty list -- this seed no longer applies; it only fires the very first time.
     */
    val trackers: Flow<List<Tracker>> = context.trackerDataStore.data.map { prefs ->
        val raw = prefs[trackersKey] ?: return@map defaultTrackers()
        runCatching {
            json.decodeFromString<List<TrackerDto>>(raw).map { it.toDomain() }
        }.getOrElse { emptyList() } // corrupt/unreadable data shouldn't crash the app -- just start fresh
    }

    private fun defaultTrackers(): List<Tracker> = listOf(
        Tracker(
            id = "workout",
            name = "Workout",
            icon = "workout",
            color = Color(0xFF5C8A6A),
            method = CompletionMethod.QUICK,
        ),
        Tracker(
            id = "reading",
            name = "Reading",
            icon = "reading",
            color = Color(0xFF4A7FA5),
            method = CompletionMethod.QUICK,
        ),
        Tracker(
            id = "swimming",
            name = "Swimming",
            icon = "swimming",
            color = Color(0xFF3E9C96),
            method = CompletionMethod.QUICK,
        ),
    )

    suspend fun save(trackers: List<Tracker>) {
        context.trackerDataStore.edit { prefs ->
            prefs[trackersKey] = json.encodeToString(trackers.map { it.toDto() })
        }
    }
}