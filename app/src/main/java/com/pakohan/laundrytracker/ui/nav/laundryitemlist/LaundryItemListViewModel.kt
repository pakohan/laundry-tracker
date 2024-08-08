package com.pakohan.laundrytracker.ui.nav.laundryitemlist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class UiState(
    val showInputDialog: Boolean = false,
    val dialogInputText: String = "",
)

class LaundryItemListViewModel(private val laundryItemRepository: LaundryItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val laundryItems: StateFlow<List<LaundryItem>>
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
