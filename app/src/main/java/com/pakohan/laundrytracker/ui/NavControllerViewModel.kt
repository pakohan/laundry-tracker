package com.pakohan.laundrytracker.ui

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pakohan.laundrytracker.ui.nav.NavigationDestination
import com.pakohan.laundrytracker.ui.nav.TabNavigationDestination
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunlist.LaundryRunListDestination
import com.pakohan.laundrytracker.ui.nav.preferences.PreferencesDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val destinations: List<NavigationDestination> = listOf(
    LaundryItemListDestination,
    LaundryRunListDestination,
    LaundryRunItemListDestination,
    PreferencesDestination,
)

val tabs: List<TabNavigationDestination> = listOf(
    LaundryItemListDestination,
    LaundryRunListDestination,
    PreferencesDestination,
)

open class NavControllerViewModel(
    private val navController: NavController,
) : ViewModel() {
    private val _location: MutableStateFlow<Location> = MutableStateFlow(Location())
    val locationStateFlow: StateFlow<Location> = _location.asStateFlow()

    init {
        viewModelScope.launch {
            collectLocation()
        }
    }

    private suspend fun collectLocation() = navController.currentBackStackEntryFlow.collect {
        val destination = destinations.firstOrNull { dest ->
            dest.route == it.destination.route
        }

        _location.value = Location(
            destination = destination,
            arguments = it.arguments,
        )
    }

    data class Location(
        var destination: NavigationDestination? = null,
        var arguments: Bundle? = null,
    )
}
