package com.example.workoutcalender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.workoutcalender.data.ThemePreferences
import com.example.workoutcalender.data.TrackerRepository
import com.example.workoutcalender.model.CompletionMethod
import com.example.workoutcalender.model.Tracker
import com.example.workoutcalender.model.TrackerEntry
import com.example.workoutcalender.ui.components.AppScreen
import com.example.workoutcalender.ui.components.BottomNav
import com.example.workoutcalender.ui.components.GridDisplayMode
import com.example.workoutcalender.ui.screens.CreateTrackerScreen
import com.example.workoutcalender.ui.screens.DetailedEntryScreen
import com.example.workoutcalender.ui.screens.EditTrackerScreen
import com.example.workoutcalender.ui.screens.HomeScreen
import com.example.workoutcalender.ui.screens.SettingsScreen
import com.example.workoutcalender.ui.screens.TrackerDetailScreen
import com.example.workoutcalender.ui.screens.TrackersScreen
import com.example.workoutcalender.ui.theme.AppColorTheme
import com.example.workoutcalender.ui.theme.ConsistencyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Backed by [TrackerRepository] (DataStore + JSON) instead of in-memory sample data.
 * Starts as an empty list until the first DataStore read completes -- a fresh install
 * has zero trackers, same as any real app, rather than pre-seeded demo data.
 */
class TrackerState(
    private val repository: TrackerRepository,
    private val scope: CoroutineScope,
) {
    var trackers by mutableStateOf<List<Tracker>>(emptyList())
        private set

    /** Call once per app session (see the LaunchedEffect in ConsistencyApp). */
    suspend fun observe() {
        repository.trackers.collect { trackers = it }
    }

    private fun update(newTrackers: List<Tracker>) {
        trackers = newTrackers
        scope.launch { repository.save(newTrackers) }
    }

    fun toggleDay(trackerId: String, date: LocalDate) {
        update(trackers.map { if (it.id == trackerId) it.toggled(date) else it })
    }

    fun saveEntry(trackerId: String, date: LocalDate, entry: TrackerEntry) {
        update(trackers.map { if (it.id == trackerId) it.withEntry(date, entry) else it })
    }

    fun addTracker(name: String, icon: String, color: Color, method: CompletionMethod, targetPerWeek: Int) {
        update(
            trackers + Tracker(
                id = "$name-${System.currentTimeMillis()}",
                name = name,
                icon = icon,
                color = color,
                method = method,
                targetPerWeek = targetPerWeek,
            ),
        )
    }

    fun updateTracker(trackerId: String, name: String, icon: String, color: Color, method: CompletionMethod, targetPerWeek: Int) {
        update(
            trackers.map {
                if (it.id == trackerId) {
                    it.copy(name = name, icon = icon, color = color, method = method, targetPerWeek = targetPerWeek)
                } else it
            },
        )
    }

    fun deleteTracker(trackerId: String) {
        update(trackers.filterNot { it.id == trackerId })
    }
}

/** Every screen the app can show. A simple stack (see [ConsistencyApp]) drives back-navigation. */
private sealed class Screen {
    data class Tab(val tab: AppScreen) : Screen()
    data class TrackerDetail(val trackerId: String) : Screen()
    object CreateTracker : Screen()
    data class EditTracker(val trackerId: String) : Screen()
    data class DetailedEntry(val trackerId: String, val date: LocalDate) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // Dark mode and color theme are both persisted via ThemePreferences.
            var darkMode by remember { mutableStateOf(false) }

            val themePreferences = remember { ThemePreferences(context.applicationContext) }
            var colorTheme by remember { mutableStateOf(AppColorTheme.GREEN) }
            LaunchedEffect(Unit) {
                themePreferences.colorTheme.collect { colorTheme = it }
            }
            LaunchedEffect(Unit) {
                themePreferences.darkMode.collect { darkMode = it }
            }

            val view = LocalView.current
            SideEffect {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkMode
                    isAppearanceLightNavigationBars = !darkMode
                }
            }

            ConsistencyTheme(colorTheme = colorTheme, darkTheme = darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConsistencyApp(
                        darkMode = darkMode,
                        onDarkModeChange = { enabled ->
                            darkMode = enabled
                            scope.launch { themePreferences.setDarkMode(enabled) }
                        },
                        colorTheme = colorTheme,
                        onColorThemeChange = { theme ->
                            colorTheme = theme
                            scope.launch { themePreferences.setColorTheme(theme) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ConsistencyApp(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    colorTheme: AppColorTheme,
    onColorThemeChange: (AppColorTheme) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { TrackerRepository(context.applicationContext) }
    val state = remember { TrackerState(repository, scope) }

    // Starts collecting from DataStore as soon as this composable enters, and keeps
    // `state.trackers` in sync with whatever's on disk for the lifetime of the app.
    LaunchedEffect(Unit) { state.observe() }

    var displayMode by remember { mutableStateOf(GridDisplayMode.NORMAL) }

    // Simple back-stack: bottom-nav taps reset to the root of that tab; opening a
    // tracker, the create-tracker form, an edit form, or a detailed entry pushes
    // on top of it.
    val stack = remember { mutableStateListOf<Screen>(Screen.Tab(AppScreen.HOME)) }
    val current = stack.last()

    fun push(screen: Screen) { stack.add(screen) }
    fun pop() { if (stack.size > 1) stack.removeAt(stack.lastIndex) }
    fun switchTab(tab: AppScreen) {
        stack.clear()
        stack.add(Screen.Tab(tab))
    }

    val currentTab = (stack.firstOrNull { it is Screen.Tab } as? Screen.Tab)?.tab ?: AppScreen.HOME
    val showBottomNav = current is Screen.Tab

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNav(current = currentTab, onSelect = ::switchTab)
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (val screen = current) {
                    is Screen.Tab -> when (screen.tab) {
                        AppScreen.HOME -> HomeScreen(
                            trackers = state.trackers,
                            onToggleToday = { state.toggleDay(it.id, LocalDate.now()) },
                            onOpenTracker = { push(Screen.TrackerDetail(it.id)) },
                            onAddTracker = { push(Screen.CreateTracker) },
                        )
                        AppScreen.TRACKERS -> TrackersScreen(
                            trackers = state.trackers,
                            onOpenTracker = { push(Screen.TrackerDetail(it.id)) },
                            onAddTracker = { push(Screen.CreateTracker) },
                            onDeleteTracker = { state.deleteTracker(it.id) },
                        )
                        AppScreen.SETTINGS -> SettingsScreen(
                            darkMode = darkMode,
                            onDarkModeChange = onDarkModeChange,
                            displayMode = displayMode,
                            onDisplayModeChange = { displayMode = it },
                            colorTheme = colorTheme,
                            onColorThemeChange = onColorThemeChange,
                        )
                    }

                    is Screen.TrackerDetail -> {
                        val tracker = state.trackers.find { it.id == screen.trackerId }
                        if (tracker != null) {
                            TrackerDetailScreen(
                                tracker = tracker,
                                displayMode = displayMode,
                                onBack = ::pop,
                                onEdit = { push(Screen.EditTracker(tracker.id)) },
                                onQuickToggle = { date -> state.toggleDay(tracker.id, date) },
                                onOpenDetailedEntry = { date -> push(Screen.DetailedEntry(tracker.id, date)) },
                            )
                        }
                    }

                    Screen.CreateTracker -> CreateTrackerScreen(
                        onBack = ::pop,
                        onCreate = { name, icon, color, method, targetPerWeek ->
                            state.addTracker(name, icon, color, method, targetPerWeek)
                            pop()
                        },
                    )

                    is Screen.EditTracker -> {
                        val tracker = state.trackers.find { it.id == screen.trackerId }
                        if (tracker != null) {
                            EditTrackerScreen(
                                tracker = tracker,
                                onBack = ::pop,
                                onSave = { name, icon, color, method, targetPerWeek ->
                                    state.updateTracker(tracker.id, name, icon, color, method, targetPerWeek)
                                    pop()
                                },
                            )
                        }
                    }

                    is Screen.DetailedEntry -> {
                        val tracker = state.trackers.find { it.id == screen.trackerId }
                        if (tracker != null) {
                            DetailedEntryScreen(
                                tracker = tracker,
                                date = screen.date,
                                onBack = ::pop,
                                onSave = { entry ->
                                    state.saveEntry(tracker.id, screen.date, entry)
                                    pop()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}