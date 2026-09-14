package com.example.workoutcalender.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.DisplayFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Prev/next month navigation, e.g. "‹  September 2026  ›". [canGoNext] dims and
 * disables the forward arrow -- used so you can't navigate into the future, since
 * there's nothing to show past the current month.
 */
@Composable
fun MonthNavRow(
    yearMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoNext: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConsistencyColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "‹",
            fontFamily = BodyFont,
            fontSize = 20.sp,
            color = colors.textDim,
            modifier = Modifier
                .clickable(onClick = onPrevious)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + yearMonth.year,
            fontFamily = BodyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = colors.text,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Text(
            "›",
            fontFamily = BodyFont,
            fontSize = 20.sp,
            color = if (canGoNext) colors.textDim else colors.border,
            modifier = Modifier
                .clickable(enabled = canGoNext, onClick = onNext)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}

/** Same idea as [MonthNavRow] but for a bare year, used by yearly views. */
@Composable
fun YearNavRow(
    year: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoNext: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConsistencyColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "‹",
            fontFamily = BodyFont,
            fontSize = 20.sp,
            color = colors.textDim,
            modifier = Modifier
                .clickable(onClick = onPrevious)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Text(
            text = year.toString(),
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = colors.text,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Text(
            "›",
            fontFamily = BodyFont,
            fontSize = 20.sp,
            color = if (canGoNext) colors.textDim else colors.border,
            modifier = Modifier
                .clickable(enabled = canGoNext, onClick = onNext)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}