package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.EntryItem
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.TrackerEntry
import com.example.workoutcalender.ui.components.ScreenHeader
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DetailedEntryScreen(
    tracker: Tracker,
    date: LocalDate,
    onBack: () -> Unit,
    onSave: (TrackerEntry) -> Unit,
) {
    val colors = LocalConsistencyColors.current
    val existing = tracker.entries[date]
    val rows = remember {
        (existing?.items?.takeIf { it.isNotEmpty() } ?: listOf(EntryItem("Squats", "3 × 5"), EntryItem("Push-ups", "3 × 5")))
            .toMutableStateList()
    }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    val dateLabel = date.format(DateTimeFormatter.ofPattern("MMM d"))

    LazyColumn(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            ScreenHeader(title = "${tracker.name} — $dateLabel", onBack = onBack)
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                rows.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = item.name,
                            onValueChange = { rows[index] = item.copy(name = it) },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = item.value,
                            onValueChange = { rows[index] = item.copy(value = it) },
                            placeholder = { Text("3 × 5", fontFamily = BodyFont) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                }

                Text(
                    text = "+ Add exercise",
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = tracker.color,
                    modifier = Modifier
                        .clickable { rows.add(EntryItem("", "")) }
                        .padding(vertical = 8.dp),
                )

                Text(
                    text = "NOTES",
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = colors.textDim,
                    modifier = Modifier.padding(top = 14.dp, bottom = 10.dp),
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Felt good today.", fontFamily = BodyFont) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tracker.color)
                        .clickable {
                            onSave(TrackerEntry(items = rows.filter { it.name.isNotBlank() }, notes = notes))
                        }
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text("Save", fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}
