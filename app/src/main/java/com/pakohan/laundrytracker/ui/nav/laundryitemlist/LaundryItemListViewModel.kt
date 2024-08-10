package com.pakohan.laundrytracker.ui.nav.laundryitemlist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
internal data class UiState(
    val laundryItem: EnrichedLaundryItem? = null,
)

class LaundryItemListViewModel(private val laundryItemRepository: LaundryItemRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    internal val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val laundryItems: StateFlow<List<EnrichedLaundryItem>> = laundryItemRepository.getAllFlow()
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

    fun hideDialog() = _uiState.update {
        it.copy(laundryItem = null)
    }

    fun showDialog(laundryItem: EnrichedLaundryItem) = _uiState.update {
        it.copy(laundryItem = laundryItem)
    }

    fun editLaundryItem(name: String) {
        val laundryItem = uiState.value.laundryItem
        if (laundryItem != null) viewModelScope.launch {
            laundryItemRepository.update(
                LaundryItem(
                    id = laundryItem.id,
                    name = name,
                ),
            )
        }
    }
}
