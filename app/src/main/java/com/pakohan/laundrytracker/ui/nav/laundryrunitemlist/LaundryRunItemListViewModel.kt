package com.pakohan.laundrytracker.ui.nav.laundryrunitemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryRunItem
import com.pakohan.laundrytracker.data.entity.LaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryRunItem
import com.pakohan.laundrytracker.data.entity.LaundryRunItemRepository
import com.pakohan.laundrytracker.data.entity.LaundryRunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LaundryRunItemListViewModel(
    laundryRunId: Int,
    laundryRunRepository: LaundryRunRepository,
    private val laundryRunItemRepository: LaundryRunItemRepository,
) : ViewModel() {
    val laundryRunItems: StateFlow<List<EnrichedLaundryRunItem>> =
        laundryRunItemRepository.getAllOfRunFlow(laundryRunId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList(),
            )
    val laundryRun: StateFlow<LaundryRun?> = laundryRunRepository.getFlow(laundryRunId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LaundryRun(),
        )

    fun increment(
        enrichedLaundryRunItem: EnrichedLaundryRunItem,
        delta: Int,
    ) {
        val newItem = LaundryRunItem(
            laundryRunId = enrichedLaundryRunItem.laundryRunId,
            laundryItemId = enrichedLaundryRunItem.laundryItemId,
            quantity = enrichedLaundryRunItem.quantity + delta,
        )
        viewModelScope.launch {
            if (newItem.quantity == 0) {
                laundryRunItemRepository.delete(newItem)
            } else {
                laundryRunItemRepository.create(newItem)
            }
        }
    }

    fun incrementRetrieved(
        enrichedLaundryRunItem: EnrichedLaundryRunItem,
        delta: Int,
    ) {
        val newItem = LaundryRunItem(
            laundryRunId = enrichedLaundryRunItem.laundryRunId,
            laundryItemId = enrichedLaundryRunItem.laundryItemId,
            quantity = enrichedLaundryRunItem.quantity,
            retrievedQuantity = enrichedLaundryRunItem.retrievedQuantity + delta,
        )
        viewModelScope.launch {
            if (newItem.quantity == 0) {
                laundryRunItemRepository.delete(newItem)
            } else {
                laundryRunItemRepository.create(newItem)
            }
        }
    }
}
