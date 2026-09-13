package com.example.workoutcalender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors

enum class AppScreen { HOME, TRACKERS, SETTINGS }

@Composable
fun BottomNav(current: AppScreen, onSelect: (AppScreen) -> Unit) {
    val colors = LocalConsistencyColors.current
    val items: List<Triple<AppScreen, String, ImageVector>> = listOf(
        Triple(AppScreen.HOME, "Home", Icons.Filled.Home),
        Triple(AppScreen.TRACKERS, "Trackers", Icons.Filled.DateRange),
        Triple(AppScreen.SETTINGS, "Settings", Icons.Filled.Settings),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 1.dp, color = colors.border)
            .navigationBarsPadding()
            .padding(top = 10.dp, bottom = 10.dp),
    ) {
        items.forEach { (screen, label, icon) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(screen) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (current == screen) colors.text else colors.textDim,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    label,
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (current == screen) colors.text else colors.textDim,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}
