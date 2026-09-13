package com.example.workoutcalender.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutcalender.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = ThemePreferences(application)

    val colorTheme: StateFlow<AppColorTheme> = prefs.colorTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppColorTheme.GREEN)

    fun setColorTheme(theme: AppColorTheme) {
        viewModelScope.launch { prefs.setColorTheme(theme) }
    }
}