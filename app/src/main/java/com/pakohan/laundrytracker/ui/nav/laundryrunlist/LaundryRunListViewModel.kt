package com.pakohan.laundrytracker.ui.nav.laundryrunlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.DataStoreUserPreferencesRepository
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryRunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class LaundryRunListViewModel(
    private val laundryRunRepository: LaundryRunRepository,
    private val preferencesRepository: DataStoreUserPreferencesRepository,
) : ViewModel() {
    val laundryItems: StateFlow<List<EnrichedLaundryRun>>
        get() = laundryRunRepository.getAllFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList(),
            )

    fun delete(laundryRun: EnrichedLaundryRun) {
        viewModelScope.launch { laundryRunRepository.delete(LaundryRun(id = laundryRun.id)) }
    }

    fun startRun(
        context: Context,
        laundryRun: EnrichedLaundryRun,
    ) {
        viewModelScope.launch {
            laundryRunRepository.update(
                LaundryRun(
                    id = laundryRun.id,
                    startedAt = Date(),
                ),
            )
            preferencesRepository.startTimer(context)
        }
    }
}
