package com.pakohan.laundrytracker.ui.nav.laundryitemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LaundryItemListViewModel(private val laundryItemRepository: LaundryItemRepository) : ViewModel() {
    val laundryItems: StateFlow<List<EnrichedLaundryItem>>
        get() = laundryItemRepository.getAllFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList(),
            )

    fun delete(laundryItem: LaundryItem) {
        viewModelScope.launch {
            laundryItemRepository.delete(laundryItem)
        }
    }
}
