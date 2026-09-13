package com.example.workoutcalender.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.DisplayFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.YearMonth

/**
 * Statistics grid: this month / total completed / current streak / best streak,
 * plus a "this week" progress box for non-daily goals. Matches the bordered-box
 * style used for the reference screenshot.
 */
@Composable
fun StatBlock(tracker: Tracker, modifier: Modifier = Modifier) {
    val now = YearMonth.now()
    val thisMonth = tracker.completedInMonth(now.year, now.monthValue)
    val total = tracker.completedDates.size
    val current = remember(tracker.completedDates, tracker.targetPerWeek) { tracker.currentStreak() }
    val best = remember(tracker.completedDates, tracker.targetPerWeek) { tracker.bestStreak() }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Statistics",
            fontFamily = BodyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = LocalConsistencyColors.current.textDim,
        )

        if (!tracker.isDailyGoal) {
            StatBox(
                label = "This week",
                displayValue = "${tracker.completedInWeek()}/${tracker.targetPerWeek}",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox("This month", "$thisMonth", Modifier.weight(1f))
            StatBox("Total completed", "$total", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(if (tracker.isDailyGoal) "Current streak" else "Week streak", "$current", Modifier.weight(1f))
            StatBox(if (tracker.isDailyGoal) "Best streak" else "Best week streak", "$best", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(label: String, displayValue: String, modifier: Modifier = Modifier) {
    val colors = LocalConsistencyColors.current
    Column(
        modifier = modifier
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Text(label, fontFamily = BodyFont, fontSize = 13.sp, color = colors.textDim)
        Text(
            text = displayValue,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = colors.text,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}