package com.example.workoutcalender.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/**
 * How a tracker records a day as done.
 *
 * QUICK    -> tapping a tile toggles it instantly.
 * DETAILED -> tapping a tile opens the entry screen.
 * BOTH     -> a normal tap toggles it, a long-press opens the entry screen.
 */
enum class CompletionMethod { QUICK, DETAILED, BOTH }

/** One line item inside a detailed entry, e.g. "Squats" / "3 × 5". */
data class EntryItem(
    val name: String,
    val value: String,
)

/** The optional detailed log saved for a specific date. */
data class TrackerEntry(
    val items: List<EntryItem> = emptyList(),
    val notes: String = "",
)

data class Tracker(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val method: CompletionMethod,
    val targetPerWeek: Int = 7,
    /**
     * Whether this tracker counts toward Home's overall "active days" number and
     * blended intensity grid. Defaults to true so every existing tracker keeps
     * contributing unless someone explicitly opts it out (e.g. a low-priority
     * habit they don't want skewing the main overview).
     */
    val includeInOverall: Boolean = true,
    val completedDates: Set<LocalDate> = emptySet(),
    val entries: Map<LocalDate, TrackerEntry> = emptyMap(),
) {
    val isDailyGoal: Boolean get() = targetPerWeek >= 7

    fun isCompleted(date: LocalDate) = completedDates.contains(date)

    fun toggled(date: LocalDate): Tracker =
        copy(
            completedDates = if (isCompleted(date)) completedDates - date else completedDates + date,
        )

    fun withEntry(date: LocalDate, entry: TrackerEntry): Tracker =
        copy(
            completedDates = completedDates + date,
            entries = entries + (date to entry),
        )

    /** Completions within the Sunday-Saturday week containing [date]. */
    fun completedInWeek(date: LocalDate = LocalDate.now()): Int {
        val start = weekStartOf(date)
        val end = start.plusDays(6)
        return completedDates.count { it >= start && it <= end }
    }

    /**
     * Daily goals: consecutive days ending today, same as before.
     * Non-daily goals: consecutive weeks (Sun-Sat) that hit [targetPerWeek].
     * The current in-progress week only joins the streak once it has actually
     * met the target -- falling short *so far* isn't a broken streak, it just
     * isn't finished yet, so we look at last week instead until it resolves.
     */
    fun currentStreak(today: LocalDate = LocalDate.now()): Int {
        if (isDailyGoal) {
            var streak = 0
            var day = today
            while (isCompleted(day)) {
                streak++
                day = day.minusDays(1)
            }
            return streak
        }

        var weekStart = weekStartOf(today)
        if (completedInWeek(weekStart) < targetPerWeek) {
            weekStart = weekStart.minusWeeks(1)
        }
        var streak = 0
        while (completedInWeek(weekStart) >= targetPerWeek) {
            streak++
            weekStart = weekStart.minusWeeks(1)
        }
        return streak
    }

    fun bestStreak(): Int {
        if (completedDates.isEmpty()) return 0

        if (isDailyGoal) {
            val sorted = completedDates.sorted()
            var best = 1
            var run = 1
            for (i in 1 until sorted.size) {
                run = if (sorted[i] == sorted[i - 1].plusDays(1)) run + 1 else 1
                best = maxOf(best, run)
            }
            return best
        }

        val counts = completedDates.groupingBy { weekStartOf(it) }.eachCount()
        val qualifyingWeeks = counts.filterValues { it >= targetPerWeek }.keys.sorted()
        if (qualifyingWeeks.isEmpty()) return 0
        var best = 1
        var run = 1
        for (i in 1 until qualifyingWeeks.size) {
            run = if (qualifyingWeeks[i] == qualifyingWeeks[i - 1].plusWeeks(1)) run + 1 else 1
            best = maxOf(best, run)
        }
        return best
    }

    fun completedInMonth(year: Int, month: Int): Int =
        completedDates.count { it.year == year && it.monthValue == month }
}

/** Sunday-first week start for a date -- matches ContributionGrid.kt's Sunday-first columns. */
private fun weekStartOf(date: LocalDate): LocalDate {
    val col = date.dayOfWeek.value % 7 // Mon=1..Sun=7 -> Sun=0..Sat=6
    return date.minusDays(col.toLong())
}

/** Starter presets shown on the "Create Tracker" screen. */
object TrackerPresets {
    data class Preset(val name: String, val icon: String, val color: Color)

    val all = listOf(
        Preset("Workout", "workout", Color(0xFF5C8A6A)),
        Preset("Reading", "reading", Color(0xFF4A7FA5)),
        Preset("Swimming", "swimming", Color(0xFF3E9C96)),
        Preset("Walking", "walking", Color(0xFFC68A3E)),
        Preset("Running", "running", Color(0xFFB25A4A)),
        Preset("Studying", "studying", Color(0xFF7B6BA8)),
        Preset("Meditation", "meditation", Color(0xFF8A7B9C)),
        Preset("Drinking Water", "water", Color(0xFF4A9FC4)),
        Preset("Journaling", "journaling", Color(0xFFB07A4E)),
        Preset("Sleep", "sleep", Color(0xFF6B6FA8)),
        Preset("Gratitude", "gratitude", Color(0xFFC98A5C)),
        Preset("Language Practice", "language", Color(0xFF4E9E7C)),
        Preset("Creative Time", "creative", Color(0xFFC15E82)),
        Preset("Tidying Up", "tidying", Color(0xFF7C9A6E)),
    )
}