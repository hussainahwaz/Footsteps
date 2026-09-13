
package com.example.workoutcalender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.theme.AppColorTheme
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

@Composable
fun ThemeSettingsRow(
    selected: AppColorTheme,
    onSelect: (AppColorTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConsistencyColors.current
    Column(modifier = modifier) {
        Text(
            text = "Theme",
            fontFamily = BodyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = colors.textDim,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppColorTheme.entries.forEach { theme ->
                ThemeSwatch(
                    theme = theme,
                    isSelected = theme == selected,
                    onClick = { onSelect(theme) },
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatch(
    theme: AppColorTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    // Always render from the light palette so swatches look consistent
    // regardless of whether the device is currently in dark mode.
    val palette = theme.light
    val ringColor = LocalConsistencyColors.current.text

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .then(
                    if (isSelected) Modifier.border(2.dp, ringColor, CircleShape) else Modifier
                )
                .padding(if (isSelected) 3.dp else 0.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(palette.overallAccent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        ) {}
        Spacer(Modifier.height(6.dp))
        Text(
            text = theme.label,
            fontFamily = BodyFont,
            fontSize = 10.sp,
            color = LocalConsistencyColors.current.textDim,
        )
    }
}