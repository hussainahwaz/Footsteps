package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.CompletionMethod
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.iconFor
import com.example.workoutcalender.ui.components.ReorderableTrackerList
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.DisplayFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackersScreen(
    trackers: List<Tracker>,
    onOpenTracker: (Tracker) -> Unit,
    onAddTracker: () -> Unit,
    onDeleteTracker: (Tracker) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
) {
    val colors = LocalConsistencyColors.current
    var pendingDelete by remember { mutableStateOf<Tracker?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                text = "Trackers",
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        item {
            Text(
                text = "Tap to open · Hold to delete · Drag the handle to reorder",
                fontFamily = BodyFont,
                fontSize = 12.sp,
                color = colors.textDim,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }

        item {
            ReorderableTrackerList(
                trackers = trackers,
                onMove = onReorder,
            ) { _, tracker, dragHandleModifier ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                        .combinedClickable(
                            onClick = { onOpenTracker(tracker) },
                            onLongClick = { pendingDelete = tracker },
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tracker.color),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = iconFor(tracker.icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tracker.name, fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colors.text)
                        val methodLabel = when (tracker.method) {
                            CompletionMethod.QUICK -> "Quick tap"
                            CompletionMethod.DETAILED -> "Detailed"
                            CompletionMethod.BOTH -> "Quick + Detailed"
                        }
                        Text(
                            text = "${tracker.completedDates.size} days · $methodLabel",
                            fontFamily = BodyFont,
                            fontSize = 12.sp,
                            color = colors.textDim,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .combinedClickable(onClick = onAddTracker)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("+ Add Tracker", fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.textDim)
            }
        }
    }

    val toDelete = pendingDelete
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete \"${toDelete.name}\"?", fontFamily = BodyFont, fontWeight = FontWeight.SemiBold) },
            text = {
                Text(
                    "This removes the tracker and everything logged against it. This can't be undone.",
                    fontFamily = BodyFont,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTracker(toDelete)
                    pendingDelete = null
                }) {
                    Text("Delete", fontFamily = BodyFont, color = Color(0xFFB4483E), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel", fontFamily = BodyFont)
                }
            },
        )
    }
}