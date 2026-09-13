package com.example.workoutcalender.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.DisplayFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

/**
 * The large centered number used on Home, Monthly, and per-tracker screens.
 * Deliberately oversized relative to the label — the number is the point.
 */
@Composable
fun BigNumber(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val colors = LocalConsistencyColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 160.sp,
            letterSpacing = (-1.5).sp,
            color = colors.text,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            fontFamily = BodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 1.5.sp,
            color = colors.textDim,
            modifier = Modifier.padding(top = 6.dp),
        )
        subtitle?.let {
            Text(
                text = it,
                fontFamily = BodyFont,
                fontSize = 13.sp,
                color = colors.textDim,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}
