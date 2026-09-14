package com.example.workoutcalender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.ui.theme.BodyFont
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

/**
 * Reverse-chronological list of every logged [TrackerEntry] for a Detailed/Both
 * tracker -- lets someone review what they wrote on a past date without paging
 * through the calendar grid. Only meaningful for trackers whose method actually
 * produces entries; the caller decides when to show this (see TrackerDetailScreen).
 */
@Composable
fun EntryHistoryList(
    tracker: Tracker,
    onOpenEntry: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConsistencyColors.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    // Only dates with a *real* entry (items or notes) show up here -- a date that's
    // just marked complete via quick-tap on a BOTH tracker, with no detailed entry
    // ever saved, has nothing to review.
    val sortedEntries = tracker.entries.entries
        .filter { (_, entry) -> entry.items.isNotEmpty() || entry.notes.isNotBlank() }
        .sortedByDescending { it.key }

    if (sortedEntries.isEmpty()) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = "No detailed entries yet",
                fontFamily = BodyFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colors.text,
            )
            Text(
                text = "Logged entries will show up here so you can look back on them.",
                fontFamily = BodyFont,
                fontSize = 13.sp,
                color = colors.textDim,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        sortedEntries.forEachIndexed { index, (date, entry) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (index == sortedEntries.lastIndex) 0.dp else 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable { onOpenEntry(date) }
                    .padding(16.dp),
            ) {
                Text(
                    text = date.format(dateFormatter),
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colors.text,
                )

                if (entry.items.isNotEmpty()) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        entry.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(item.name, fontFamily = BodyFont, fontSize = 13.sp, color = colors.textDim)
                                Text(item.value, fontFamily = BodyFont, fontSize = 13.sp, color = colors.text)
                            }
                        }
                    }
                }

                if (entry.notes.isNotBlank()) {
                    Text(
                        text = entry.notes,
                        fontFamily = BodyFont,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = colors.textDim,
                        modifier = Modifier.padding(top = if (entry.items.isNotEmpty()) 10.dp else 8.dp),
                    )
                }
            }
        }
    }
}