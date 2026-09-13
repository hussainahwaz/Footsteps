package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.iconFor
import com.example.workoutcalender.ui.components.BigNumber
import com.example.workoutcalender.ui.components.OverallMonthGrid
import com.example.workoutcalender.ui.components.ReorderableTrackerList
import com.example.workoutcalender.ui.components.Tile
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    trackers: List<Tracker>,
    onToggleToday: (Tracker) -> Unit,
    onOpenTracker: (Tracker) -> Unit,
    onAddTracker: () -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
) {
    val colors = LocalConsistencyColors.current
    val currentMonth = YearMonth.now()
    val activeDays = trackers.flatMap { it.completedDates }
        .filter { it.year == currentMonth.year && it.monthValue == currentMonth.monthValue }
        .distinct()
        .size

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),

        ) {
        item {
            BigNumber(
                value = activeDays,
                label = "ACTIVE DAYS",
                subtitle = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                modifier = Modifier.fillMaxWidth().padding(top = 100.dp, bottom = 20.dp),
            )
        }

        item {
            // The Home overall grid always shows multi-tracker intensity, regardless of
            // the per-tracker display mode setting — that setting only affects the
            // per-tracker screens where a single tracker's own tiles are shown.
            OverallMonthGrid(
                yearMonth = currentMonth,
                trackers = trackers,
                tileSize = 48.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            )
        }

        item {
            ReorderableTrackerList(
                trackers = trackers,
                onMove = onReorder,
            ) { _, tracker, dragHandleModifier ->
                TrackerRow(
                    tracker = tracker,
                    onOpen = { onOpenTracker(tracker) },
                    onToggleToday = { onToggleToday(tracker) },
                    dragHandleModifier = dragHandleModifier,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable(onClick = onAddTracker)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("+ Add Tracker", fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.textDim)
            }
        }
    }
}

@Composable
private fun TrackerRow(
    tracker: Tracker,
    onOpen: () -> Unit,
    onToggleToday: () -> Unit,
    dragHandleModifier: Modifier,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConsistencyColors.current
    val today = LocalDate.now()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = iconFor(tracker.icon),
                contentDescription = null,
                tint = tracker.color,
                modifier = Modifier.size(20.dp),
            )
            Text(tracker.name, fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = colors.text)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("${tracker.completedDates.size} days", fontFamily = BodyFont, fontSize = 13.sp, color = colors.textDim)
            Tile(
                filled = tracker.isCompleted(today),
                color = tracker.color,
                size = 26.dp,
                cornerRadius = 7.dp,
                onTap = onToggleToday,
            )
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Drag to reorder",
                tint = colors.textDim,
                modifier = Modifier
                    .size(20.dp)
                    .then(dragHandleModifier)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {},
            )
        }
    }
}