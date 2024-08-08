package com.pakohan.laundrytracker.ui.nav

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.ui.NavControllerViewModel
import com.pakohan.laundrytracker.ui.tabs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class UiState(
    val selectedTab: TabNavigationDestination? = null,
    @StringRes val currentTitle: Int,
) {
    val showBackNavigation: Boolean = selectedTab == null
}

class MainScaffoldViewModel(
    navController: NavController,
) : NavControllerViewModel(navController) {
    private val _uiState = MutableStateFlow(
        UiState(
            selectedTab = null,
            currentTitle = R.string.app_name,
        ),
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateTitle()
        }
    }

    private suspend fun updateTitle(): Unit = locationStateFlow.collect { location ->
        _uiState.update {
            UiState(
                selectedTab = tabs.find { it == location.destination },
                currentTitle = location.destination?.titleRes ?: R.string.app_name,
            )
        }
    }
}
