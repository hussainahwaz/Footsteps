package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.workoutcalender.model.TrackerPresets
import com.example.workoutcalender.model.iconFor
import com.example.workoutcalender.ui.components.ScreenHeader
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

internal val TRACKER_FORM_PALETTE = listOf(
    Color(0xFF5C8A6A), Color(0xFF4A7FA5), Color(0xFF3E9C96),
    Color(0xFFC68A3E), Color(0xFFB25A4A), Color(0xFF7B6BA8), Color(0xFF8A7B9C),
)

/**
 * Shared name/icon/color/method/frequency/overview-visibility form used by both
 * CreateTrackerScreen and EditTrackerScreen -- only the screen title, starting
 * values, and submit label/callback differ between the two.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TrackerForm(
    screenTitle: String,
    initialName: String,
    initialIcon: String,
    initialColor: Color,
    initialMethod: CompletionMethod,
    initialTargetPerWeek: Int,
    initialIncludeInOverall: Boolean,
    submitLabel: String,
    onBack: () -> Unit,
    onSubmit: (name: String, icon: String, color: Color, method: CompletionMethod, targetPerWeek: Int, includeInOverall: Boolean) -> Unit,
) {
    val colors = LocalConsistencyColors.current
    var name by remember { mutableStateOf(initialName) }
    var icon by remember { mutableStateOf(initialIcon) }
    var color by remember { mutableStateOf(initialColor) }
    var method by remember { mutableStateOf(initialMethod) }
    var targetPerWeek by remember { mutableStateOf(initialTargetPerWeek) }
    var includeInOverall by remember { mutableStateOf(initialIncludeInOverall) }
    var selectedPreset by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { ScreenHeader(title = screenTitle, onBack = onBack) }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionLabel("PRESETS")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrackerPresets.all.forEach { preset ->
                        val selected = selectedPreset == preset.name
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) preset.color.copy(alpha = 0.15f) else Color.Transparent)
                                .border(1.dp, if (selected) preset.color else colors.border, RoundedCornerShape(20.dp))
                                .clickable {
                                    selectedPreset = preset.name
                                    name = preset.name
                                    icon = preset.icon
                                    color = preset.color
                                }
                                .padding(horizontal = 13.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = iconFor(preset.icon),
                                contentDescription = null,
                                tint = preset.color,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(preset.name, fontFamily = BodyFont, fontSize = 13.sp, color = colors.text)
                        }
                    }
                }

                SectionLabel("NAME", topPadding = 22.dp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; selectedPreset = null },
                    placeholder = { Text("Tracker name", fontFamily = BodyFont) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                SectionLabel("COLOR", topPadding = 22.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TRACKER_FORM_PALETTE.forEach { c ->
                        Column(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(
                                    width = if (color == c) 2.5.dp else 0.dp,
                                    color = if (color == c) colors.text else Color.Transparent,
                                    shape = CircleShape,
                                )
                                .clickable { color = c },
                        ) {}
                    }
                }

                SectionLabel("COMPLETION METHOD", topPadding = 22.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(CompletionMethod.QUICK, "Quick Tap", "Tap a tile to mark it done"),
                        Triple(CompletionMethod.DETAILED, "Detailed", "Tap opens a details entry"),
                        Triple(CompletionMethod.BOTH, "Both", "Tap to mark, hold for details"),
                    ).forEach { (m, label, desc) ->
                        val selected = method == m
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (selected) color else colors.border, RoundedCornerShape(12.dp))
                                .clickable { method = m }
                                .padding(horizontal = 15.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(label, fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.text)
                                Text(desc, fontFamily = BodyFont, fontSize = 12.sp, color = colors.textDim, modifier = Modifier.padding(top = 2.dp))
                            }
                            Column(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) color else Color.Transparent)
                                    .border(2.dp, if (selected) color else colors.border, CircleShape),
                            ) {}
                        }
                    }
                }

                SectionLabel("GOAL FREQUENCY", topPadding = 22.dp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3, 4, 5, 6, 7).forEach { n ->
                        val selected = targetPerWeek == n
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) color.copy(alpha = 0.15f) else Color.Transparent)
                                .border(1.dp, if (selected) color else colors.border, RoundedCornerShape(20.dp))
                                .clickable { targetPerWeek = n }
                                .padding(horizontal = 13.dp, vertical = 9.dp),
                        ) {
                            Text(
                                text = if (n == 7) "Daily" else "${n}x / week",
                                fontFamily = BodyFont,
                                fontSize = 13.sp,
                                color = colors.text,
                            )
                        }
                    }
                }

                SectionLabel("HOME SCREEN", topPadding = 22.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .clickable { includeInOverall = !includeInOverall }
                        .padding(horizontal = 15.dp, vertical = 13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show on Home overview", fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.text)
                        Text(
                            "Counts toward Home's active days and overall grid",
                            fontFamily = BodyFont,
                            fontSize = 12.sp,
                            color = colors.textDim,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (includeInOverall) color else Color.Transparent)
                            .border(2.dp, if (includeInOverall) color else colors.border, CircleShape),
                    ) {}
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 26.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (name.isNotBlank()) color else colors.border)
                        .clickable(enabled = name.isNotBlank()) {
                            onSubmit(name.trim(), icon, color, method, targetPerWeek, includeInOverall)
                        }
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(submitLabel, fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    val colors = LocalConsistencyColors.current
    Text(
        text = text,
        fontFamily = BodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        color = colors.textDim,
        modifier = Modifier.padding(top = topPadding, bottom = 10.dp),
    )
}