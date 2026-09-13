package com.example.workoutcalender.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.workoutcalender.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

/** Persists the user's chosen color theme and dark/light mode across app restarts. */
class ThemePreferences(private val context: Context) {

    private val colorThemeKey = intPreferencesKey("color_theme_ordinal")
    private val darkModeKey = booleanPreferencesKey("dark_mode_enabled")

    /** Defaults to GREEN if nothing's been saved yet. */
    val colorTheme: Flow<AppColorTheme> = context.themeDataStore.data.map { prefs ->
        val ordinal = prefs[colorThemeKey] ?: AppColorTheme.GREEN.ordinal
        AppColorTheme.entries.getOrElse(ordinal) { AppColorTheme.GREEN }
    }

    /** Defaults to false (light mode) if nothing's been saved yet. */
    val darkMode: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[darkModeKey] ?: false
    }

    suspend fun setColorTheme(theme: AppColorTheme) {
        context.themeDataStore.edit { prefs ->
            prefs[colorThemeKey] = theme.ordinal
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[darkModeKey] = enabled
        }
    }
}
