package com.example.workoutcalender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.model.CompletionMethod
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.iconFor
import com.example.workoutcalender.ui.components.BigNumber
import com.example.workoutcalender.ui.components.EntryHistoryList
import com.example.workoutcalender.ui.components.GridDisplayMode
import com.example.workoutcalender.ui.components.MonthGrid
import com.example.workoutcalender.ui.components.MonthNavRow
import com.example.workoutcalender.ui.components.ScreenHeader
import com.example.workoutcalender.ui.components.StatBlock
import com.example.workoutcalender.ui.components.YearNavRow
import com.example.workoutcalender.ui.components.YearlyGrid
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import java.time.LocalDate
import java.time.YearMonth

private enum class DetailView { MONTHLY, YEARLY, HISTORY }

@Composable
fun TrackerDetailScreen(
    tracker: Tracker,
    displayMode: GridDisplayMode,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onQuickToggle: (LocalDate) -> Unit,
    onOpenDetailedEntry: (LocalDate) -> Unit,
) {
    val colors = LocalConsistencyColors.current
    val today = YearMonth.now()

    // History only makes sense for trackers that actually produce entries.
    val hasHistory = tracker.method == CompletionMethod.DETAILED || tracker.method == CompletionMethod.BOTH

    var view by remember { mutableStateOf(DetailView.MONTHLY) }
    var selectedMonth by remember { mutableStateOf(today) }
    var selectedYear by remember { mutableStateOf(today.year) }

    fun handleTap(date: LocalDate) {
        when (tracker.method) {
            CompletionMethod.QUICK -> onQuickToggle(date)
            CompletionMethod.DETAILED -> onOpenDetailedEntry(date)
            CompletionMethod.BOTH -> onQuickToggle(date)
        }
    }

    fun handleLongPress(date: LocalDate) {
        if (tracker.method == CompletionMethod.BOTH) onOpenDetailedEntry(date)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            ScreenHeader(
                title = tracker.name,
                onBack = onBack,
                trailing = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Edit tracker",
                            tint = colors.textDim,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onEdit() },
                        )
                        Icon(
                            imageVector = iconFor(tracker.icon),
                            contentDescription = null,
                            tint = tracker.color,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tabs = buildList {
                        add(DetailView.MONTHLY to "Monthly")
                        add(DetailView.YEARLY to "Yearly")
                        if (hasHistory) add(DetailView.HISTORY to "History")
                    }
                    tabs.forEach { (v, label) ->
                        val selected = view == v
                        Text(
                            text = label,
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selected) androidx.compose.ui.graphics.Color.White else colors.textDim,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) tracker.color else androidx.compose.ui.graphics.Color.Transparent)
                                .border(
                                    width = if (selected) 0.dp else 1.dp,
                                    color = colors.border,
                                    shape = RoundedCornerShape(20.dp),
                                )
                                .clickable { view = v }
                                .padding(horizontal = 16.dp, vertical = 7.dp),
                        )
                    }
                }
            }
        }

        item {
            when (view) {
                DetailView.MONTHLY -> Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                ) {
                    BigNumber(
                        value = tracker.completedInMonth(selectedMonth.year, selectedMonth.monthValue),
                        label = "ACTIVE DAYS",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    )
                    MonthNavRow(
                        yearMonth = selectedMonth,
                        onPrevious = { selectedMonth = selectedMonth.minusMonths(1) },
                        onNext = { selectedMonth = selectedMonth.plusMonths(1) },
                        canGoNext = selectedMonth < today,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                    MonthGrid(
                        yearMonth = selectedMonth,
                        tracker = tracker,
                        displayMode = displayMode,
                        onDayTap = ::handleTap,
                        onDayLongPress = ::handleLongPress,
                    )
                    if (tracker.method == CompletionMethod.BOTH) {
                        Text(
                            text = "Tap to mark complete · Hold for details",
                            fontFamily = BodyFont,
                            fontSize = 12.sp,
                            color = colors.textDim,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }
                DetailView.YEARLY -> Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                ) {
                    YearNavRow(
                        year = selectedYear,
                        onPrevious = { selectedYear -= 1 },
                        onNext = { selectedYear += 1 },
                        canGoNext = selectedYear < today.year,
                        modifier = Modifier.padding(bottom = 18.dp),
                    )
                    YearlyGrid(
                        year = selectedYear,
                        tracker = tracker,
                        tappable = false,
                        onDayTap = null,
                    )
                }
                DetailView.HISTORY -> Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    EntryHistoryList(
                        tracker = tracker,
                        onOpenEntry = onOpenDetailedEntry,
                    )
                }
            }
        }

        item {
            StatBlock(tracker = tracker, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
        }
    }
}