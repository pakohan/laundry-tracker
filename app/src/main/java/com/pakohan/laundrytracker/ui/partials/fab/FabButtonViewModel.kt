package com.pakohan.laundrytracker.ui.partials.fab

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.pakohan.laundrytracker.data.entity.DataStoreUserPreferencesRepository
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItemRepository
import com.pakohan.laundrytracker.data.entity.LaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryRunRepository
import com.pakohan.laundrytracker.ui.NavControllerViewModel
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunlist.LaundryRunListDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

@Immutable
data class UiState(
    val icon: ImageVector,
    val onClick: (Context) -> Unit,
    val showDialog: Boolean = false,
    val inputText: String = "",
    val laundryRun: EnrichedLaundryRun? = null,
)

class FabButtonViewModel(
    private val navController: NavHostController,
    private val laundryItemRepository: LaundryItemRepository,
    private val laundryRunRepository: LaundryRunRepository,
    private val preferencesRepository: DataStoreUserPreferencesRepository,
) : NavControllerViewModel(
    navController,
) {
    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiState?> = _uiState.asStateFlow()

    private val laundryRuns: StateFlow<List<EnrichedLaundryRun>> = laundryRunRepository.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch { updateLocation() }
        viewModelScope.launch { updateLaundryRunFab() }
    }

    private suspend fun updateLocation(): Unit = locationStateFlow.collect { location ->
        setCurrentUiState(location)
    }

    private suspend fun updateLaundryRunFab(): Unit = laundryRuns.collect {
        val location = locationStateFlow.value
        if (location.destination == LaundryRunItemListDestination) {
            _uiState.update { getLaundryRunUiState(location) }
        }
    }

    private fun setCurrentUiState(location: Location) = _uiState.update {
        when (location.destination) {
            LaundryItemListDestination -> UiState(
                icon = Icons.Filled.Add,
                onClick = ::showAddLaundryItemDialog,
            )

            LaundryRunListDestination -> UiState(
                icon = Icons.Filled.Add,
                onClick = ::addLaundryRun,
            )

            LaundryRunItemListDestination -> {
                getLaundryRunUiState(location)
            }

            else -> null
        }
    }

    private fun getLaundryRunUiState(location: Location): UiState? {
        val id = location.arguments?.getInt(LaundryRunItemListDestination.LAUNDRY_RUN_ID)
        val laundryRun = laundryRuns.value.find { it.id == id }
        if (laundryRun == null) {
            return null
        } else if (laundryRun.startedAt == null) {
            return UiState(
                icon = Icons.Filled.PlayArrow,
                onClick = ::startLaundryRun,
                laundryRun = laundryRun,
            )
        } else if (laundryRun.retrievedQuantity == laundryRun.quantity) {
            return UiState(
                icon = Icons.Filled.Delete,
                onClick = ::deleteLaundryRun,
                laundryRun = laundryRun,
            )
        }

        return null
    }

    private fun showAddLaundryItemDialog(context: Context) = _uiState.update {
        it?.copy(showDialog = true)
    }

    private fun addLaundryRun(context: Context) {
        viewModelScope.launch {
            val id = laundryRunRepository.create(LaundryRun())
            navController.navigate(LaundryRunItemListDestination.buildRoute(id.toInt()))
        }
    }

    private fun startLaundryRun(context: Context) {
        val currentState = uiState.value
        if (currentState?.laundryRun == null) {
            return
        }

        viewModelScope.launch {
            laundryRunRepository.update(
                LaundryRun(
                    id = currentState.laundryRun.id,
                    startedAt = Date(),
                ),
            )
            _uiState.update {
                UiState(
                    icon = Icons.Filled.Delete,
                    onClick = ::deleteLaundryRun,
                    laundryRun = currentState.laundryRun.copy(startedAt = Date()),
                )
            }
            preferencesRepository.startTimer(context)
        }
    }

    private fun deleteLaundryRun(context: Context) {
        val currentState = uiState.value
        if (currentState?.laundryRun == null) {
            return
        }

        viewModelScope.launch {
            navController.navigate(LaundryRunListDestination.route)
            laundryRunRepository.delete(LaundryRun(id = currentState.laundryRun.id))
        }
    }

    fun onDialogVisibilityChange(show: Boolean) = _uiState.update {
        it?.copy(showDialog = show)
    }

    fun addLaundryItem(name: String) = viewModelScope.launch {
        laundryItemRepository.insert(LaundryItem(name = name))
    }
}
