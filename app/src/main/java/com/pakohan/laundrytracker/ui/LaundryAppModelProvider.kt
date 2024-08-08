@file:Suppress("UNCHECKED_CAST")

package com.pakohan.laundrytracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.pakohan.laundrytracker.LaundryTrackerApplication
import com.pakohan.laundrytracker.ui.nav.MainScaffoldViewModel
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemListViewModel
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemListViewModel
import com.pakohan.laundrytracker.ui.nav.laundryrunlist.LaundryRunListViewModel
import com.pakohan.laundrytracker.ui.nav.preferences.PreferencesViewModel
import com.pakohan.laundrytracker.ui.partials.fab.FabButtonViewModel

fun CreationExtras.inventoryApplication(): LaundryTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LaundryTrackerApplication)

class ScaffoldViewModelFactory(private val navController: NavHostController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return MainScaffoldViewModel(navController) as T
    }
}

class LaundryItemListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return LaundryItemListViewModel(
            laundryItemRepository = extras.inventoryApplication().container.laundryItemRepository,
        ) as T
    }
}

class LaundryRunListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return LaundryRunListViewModel(
            laundryRunRepository = extras.inventoryApplication().container.laundryRunRepository,
            preferencesRepository = extras.inventoryApplication().container.userPreferencesRepository,
        ) as T
    }
}

class LaundryRunItemListViewModelFactory(private val laundryRunId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return LaundryRunItemListViewModel(
            laundryRunId = laundryRunId,
            laundryRunRepository = extras.inventoryApplication().container.laundryRunRepository,
            laundryRunItemRepository = extras.inventoryApplication().container.laundryRunItemRepository,
        ) as T
    }
}

class FabButtonViewModelFactory(private val navController: NavHostController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return FabButtonViewModel(
            navController,
            laundryItemRepository = extras.inventoryApplication().container.laundryItemRepository,
            laundryRunRepository = extras.inventoryApplication().container.laundryRunRepository,
            preferencesRepository = extras.inventoryApplication().container.userPreferencesRepository,
        ) as T
    }
}

class PreferencesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return PreferencesViewModel(extras.inventoryApplication().container.userPreferencesRepository) as T
    }
}
