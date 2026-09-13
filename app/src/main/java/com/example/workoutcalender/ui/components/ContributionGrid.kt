package com.example.workoutcalender.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.LocalDate
import java.time.YearMonth

/** How a calendar grid should render its tiles. */
enum class GridDisplayMode { NORMAL, MINIMAL, SUPER_MINIMAL }

private val WEEKDAY_LABELS = listOf("S", "M", "T", "W", "T", "F", "S")

/** Sunday-first column index (0..6) for a given date. */
private fun columnOf(date: LocalDate): Int = date.dayOfWeek.value % 7

/**
 * One month's contribution grid for a single tracker.
 *
 * - NORMAL: weekday header row + tiles, aligned to actual weekdays, today gets a ring.
 * - MINIMAL: no weekday row, each tile shows its day-of-month number, packed
 *   left-to-right starting at day 1 -- NOT aligned to weekday columns.
 * - SUPER_MINIMAL: tiles only, nothing else, same left-packed layout as MINIMAL --
 *   not tappable by design.
 */
@Composable
fun MonthGrid(
    yearMonth: YearMonth,
    tracker: Tracker,
    displayMode: GridDisplayMode,
    modifier: Modifier = Modifier,
    tileSize: Dp = 48.dp,
    onDayTap: ((LocalDate) -> Unit)? = null,
    onDayLongPress: ((LocalDate) -> Unit)? = null,
) {
    val today = LocalDate.now()
    val firstOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()

    // Only NORMAL mode lines days up under weekday columns. Minimal / Super Minimal
    // are meant to read as a flat, left-packed pattern -- day 1 always starts in the
    // top-left cell, regardless of what weekday it actually falls on.
    val leadingBlanks = if (displayMode == GridDisplayMode.NORMAL) columnOf(firstOfMonth) else 0

    Column(modifier = modifier) {
        if (displayMode == GridDisplayMode.NORMAL) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                WEEKDAY_LABELS.forEach { label ->
                    Box(Modifier.size(tileSize), contentAlignment = Alignment.Center) {
                        Text(label, fontFamily = BodyFont, fontSize = 11.sp, color = LocalConsistencyColors.current.textDim)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        val totalCells = leadingBlanks + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = if (row == 0) 0.dp else 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - leadingBlanks + 1
                    if (day < 1 || day > daysInMonth) {
                        Box(Modifier.size(tileSize))
                        continue
                    }
                    val date = yearMonth.atDay(day)
                    val isToday = date == today

                    when (displayMode) {
                        GridDisplayMode.MINIMAL -> NumberTile(
                            day = day,
                            filled = tracker.isCompleted(date),
                            color = tracker.color,
                            size = tileSize,
                            isToday = isToday,
                            onTap = onDayTap?.let { { it(date) } },
                            onLongPress = onDayLongPress?.let { { it(date) } },
                        )
                        GridDisplayMode.SUPER_MINIMAL -> Tile(
                            filled = tracker.isCompleted(date),
                            color = tracker.color,
                            size = tileSize,
                            cornerRadius = 6.dp,
                        ) // intentionally not tappable -- this mode is for glancing, not editing
                        GridDisplayMode.NORMAL -> NumberTile(
                            day = day,
                            filled = tracker.isCompleted(date),
                            color = tracker.color,
                            size = tileSize,
                            isToday = isToday,
                            onTap = onDayTap?.let { { it(date) } },
                            onLongPress = onDayLongPress?.let { { it(date) } },
                        )
                    }
                }
            }
        }
    }
}

/**
 * The Home-screen "overall" grid: intensity reflects how many trackers were
 * completed on a given day (0..3+), blended toward a neutral accent -- this
 * answers "how consistent was I", not "how many workouts did I do".
 */
@Composable
fun OverallMonthGrid(
    yearMonth: YearMonth,
    trackers: List<Tracker>,
    modifier: Modifier = Modifier,
    tileSize: Dp = 30.dp,
) {
    val colors = LocalConsistencyColors.current
    val today = LocalDate.now()
    val firstOfMonth = yearMonth.atDay(1)
    val leadingBlanks = columnOf(firstOfMonth)
    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = leadingBlanks + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = modifier) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = if (row == 0) 0.dp else 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - leadingBlanks + 1
                    if (day < 1 || day > daysInMonth) {
                        Box(Modifier.size(tileSize))
                        continue
                    }
                    val date = yearMonth.atDay(day)
                    val count = trackers.count { it.isCompleted(date) }
                    val fraction = when {
                        count <= 0 -> 0f
                        count == 1 -> 0.35f
                        count == 2 -> 0.65f
                        else -> 1f
                    }
                    val fill = lerp(colors.tileEmpty, colors.overallAccent, fraction)
                    Tile(
                        filled = count > 0,
                        color = fill,
                        size = tileSize,
                        isToday = date == today,
                    )
                }
            }
        }
    }
}

/**
 * A full year, 12 small month blocks of 3-per-row (Jan-Mar, Apr-Jun, ...),
 * mirroring the GitHub-style yearly overview. Pass [tappable] = false for a
 * view-only yearly overview -- see TrackerDetailScreen, which always calls
 * this with tappable = false regardless of display mode.
 */
@Composable
fun YearlyGrid(
    year: Int,
    tracker: Tracker,
    modifier: Modifier = Modifier,
    tappable: Boolean = true,
    tileSize: Dp = 9.dp,
    onDayTap: ((LocalDate) -> Unit)? = null,
) {
    val colors = LocalConsistencyColors.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (rowStart in 0 until 12 step 3) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                for (m in rowStart until rowStart + 3) {
                    val ym = YearMonth.of(year, m + 1)
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = ym.month.name.take(1) + ym.month.name.drop(1).lowercase().take(2),
                            fontFamily = BodyFont,
                            fontSize = 11.sp,
                            color = colors.textDim,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                        val daysInMonth = ym.lengthOfMonth()
                        val rows = (daysInMonth + 6) / 7
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            for (r in 0 until rows) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    for (c in 0 until 7) {
                                        val day = r * 7 + c + 1
                                        if (day > daysInMonth) {
                                            Box(Modifier.size(tileSize))
                                        } else {
                                            val date = ym.atDay(day)
                                            Tile(
                                                filled = tracker.isCompleted(date),
                                                color = tracker.color,
                                                size = tileSize,
                                                cornerRadius = 3.dp,
                                                onTap = if (tappable) onDayTap?.let { { it(date) } } else null,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
