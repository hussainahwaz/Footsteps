package com.example.workoutcalender.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.components.GridDisplayMode
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.DisplayFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.workoutcalender.ui.components.ThemeSettingsRow
import com.example.workoutcalender.ui.theme.AppColorTheme

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    displayMode: GridDisplayMode,
    onDisplayModeChange: (GridDisplayMode) -> Unit,
    colorTheme: AppColorTheme,
    onColorThemeChange: (AppColorTheme) -> Unit,
) {
    val colors = LocalConsistencyColors.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        item {
            Text(
                text = "Settings",
                fontFamily = DisplayFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = colors.text,
                modifier = Modifier.padding(bottom = 22.dp),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Dark mode", fontFamily = BodyFont, fontSize = 15.sp, color = colors.text)
                SimpleSwitch(checked = darkMode, onCheckedChange = onDarkModeChange, activeColor = Color(0xFF5C8A6A))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        }

        item {
            ThemeSettingsRow(
                selected = colorTheme,
                onSelect = onColorThemeChange,
                modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
            )
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border).padding(top = 12.dp))
        }

        item {
            Text(
                text = "Calendar display",
                fontFamily = BodyFont,
                fontSize = 15.sp,
                color = colors.text,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp),
            )
        }


        items(
            listOf(
                Triple(GridDisplayMode.NORMAL, "Normal", "Weekdays and full tiles"),
                Triple(GridDisplayMode.MINIMAL, "Minimal", "Just the day numbers, no weekday row"),
                Triple(GridDisplayMode.SUPER_MINIMAL, "Super Minimal", "Tiles only — nothing else"),
            ),
        ) { (mode, label, desc) ->
            val selected = displayMode == mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (selected) Color(0xFF5C8A6A) else colors.border, RoundedCornerShape(12.dp))
                    .clickable { onDisplayModeChange(mode) }
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(label, fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.text)
                    Text(desc, fontFamily = BodyFont, fontSize = 12.sp, color = colors.textDim, modifier = Modifier.padding(top = 2.dp))
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (selected) Color(0xFF5C8A6A) else Color.Transparent)
                        .border(2.dp, if (selected) Color(0xFF5C8A6A) else colors.border, CircleShape),
                )
            }
        }

        item {
            Text(
                text = "A quiet way to see what you've done.",
                fontFamily = BodyFont,
                fontSize = 12.sp,
                color = colors.textDim,
                modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Minimal custom switch, styled to match the app rather than pulling in Material's default look. */
@Composable
private fun SimpleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, activeColor: Color) {
    val colors = LocalConsistencyColors.current
    val offset by animateDpAsState(targetValue = if (checked) 21.dp else 3.dp, animationSpec = tween(180), label = "switch")
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (checked) activeColor else colors.border)
            .clickable { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .padding(start = offset, top = 3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}
