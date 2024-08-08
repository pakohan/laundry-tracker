package com.pakohan.laundrytracker.ui.nav.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.DataStoreUserPreferencesRepository
import com.pakohan.laundrytracker.data.entity.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(private val preferencesRepository: DataStoreUserPreferencesRepository) : ViewModel() {
    val settings: StateFlow<UserPreferences> = preferencesRepository.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences(),
    )

    fun setEnableTimer(enableTimer: Boolean) = viewModelScope.launch {
        preferencesRepository.setEnableTimer(enableTimer)
    }

    fun setTimerDurationSeconds(timerDurationSeconds: Int) = viewModelScope.launch {
        preferencesRepository.setTimerDurationSeconds(timerDurationSeconds)
    }
}
