package com.example.workoutcalender.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class CompletionMethod { QUICK, DETAILED, BOTH }

data class EntryItem(
    val name: String,
    val value: String,
)

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
    /**
     * How many days a week this needs to be done -- 7 means daily. Ignored when
     * [intervalDays] is set; a tracker is either weekly-goal-based or interval-based,
     * never both.
     */
    val targetPerWeek: Int = 7,
    /**
     * For habits that don't fit a weekly cadence at all -- e.g. medicine taken every
     * 15 days. When non-null, this overrides [targetPerWeek] entirely: streaks become
     * "consecutive on-time doses" rather than "consecutive days/weeks", and stats
     * show a next-due date instead of weekly progress.
     */
    val intervalDays: Int? = null,
    val includeInOverall: Boolean = true,
    val completedDates: Set<LocalDate> = emptySet(),
    val entries: Map<LocalDate, TrackerEntry> = emptyMap(),
) {
    val isIntervalGoal: Boolean get() = intervalDays != null
    val isDailyGoal: Boolean get() = !isIntervalGoal && targetPerWeek >= 7

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

    /** Completions within the Sunday-Saturday week containing [date]. Only meaningful for weekly-goal trackers. */
    fun completedInWeek(date: LocalDate = LocalDate.now()): Int {
        val start = weekStartOf(date)
        val end = start.plusDays(6)
        return completedDates.count { it >= start && it <= end }
    }

    /** When the next dose/entry is due, for interval-based trackers. Null if never logged or not an interval tracker. */
    fun nextDueDate(): LocalDate? {
        val interval = intervalDays ?: return null
        val last = completedDates.maxOrNull() ?: return null
        return last.plusDays(interval.toLong())
    }

    /** Days remaining until due (negative if overdue). Null if never logged or not an interval tracker. */
    fun daysUntilDue(today: LocalDate = LocalDate.now()): Long? {
        val due = nextDueDate() ?: return null
        return ChronoUnit.DAYS.between(today, due)
    }

    fun isOverdue(today: LocalDate = LocalDate.now()): Boolean {
        val days = daysUntilDue(today) ?: return false
        return days < 0
    }

    /**
     * Daily goals: consecutive days ending today.
     * Weekly goals: consecutive weeks (Sun-Sat) that hit [targetPerWeek] -- the current
     * in-progress week only joins once it has actually met the target.
     * Interval goals: consecutive doses whose gap from the previous dose was within
     * [intervalDays] -- broken to 0 the moment the tracker becomes overdue.
     */
    fun currentStreak(today: LocalDate = LocalDate.now()): Int {
        if (isIntervalGoal) {
            val interval = intervalDays!!
            val sorted = completedDates.sorted()
            if (sorted.isEmpty()) return 0
            if (isOverdue(today)) return 0
            var streak = 1
            for (i in sorted.size - 1 downTo 1) {
                val gap = ChronoUnit.DAYS.between(sorted[i - 1], sorted[i])
                if (gap <= interval) streak++ else break
            }
            return streak
        }

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

        if (isIntervalGoal) {
            val interval = intervalDays!!
            val sorted = completedDates.sorted()
            var best = 1
            var run = 1
            for (i in 1 until sorted.size) {
                val gap = ChronoUnit.DAYS.between(sorted[i - 1], sorted[i])
                run = if (gap <= interval) run + 1 else 1
                best = maxOf(best, run)
            }
            return best
        }

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
    val col = date.dayOfWeek.value % 7
    return date.minusDays(col.toLong())
}

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