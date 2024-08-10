package com.pakohan.laundrytracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.pakohan.laundrytracker.data.entity.DataStoreUserPreferencesRepository
import com.pakohan.laundrytracker.data.entity.DatabaseLaundryItemRepository
import com.pakohan.laundrytracker.data.entity.DatabaseLaundryRunItemRepository
import com.pakohan.laundrytracker.data.entity.DatabaseLaundryRunRepository
import com.pakohan.laundrytracker.data.entity.LaundryItemRepository
import com.pakohan.laundrytracker.data.entity.LaundryRunItemRepository
import com.pakohan.laundrytracker.data.entity.LaundryRunRepository

interface AppContainer {
    val laundryItemRepository: LaundryItemRepository
    val laundryRunRepository: LaundryRunRepository
    val laundryRunItemRepository: LaundryRunItemRepository
    val userPreferencesRepository: DataStoreUserPreferencesRepository
}

class AppDataContainer(
    private val context: Context,
    dataStore: DataStore<Preferences>,
) : AppContainer {
    override val laundryItemRepository: LaundryItemRepository by lazy {
        DatabaseLaundryItemRepository(
            AppDatabase.getDatabase(context)
                .laundryItemDao(),
        )
    }
    override val laundryRunRepository: LaundryRunRepository by lazy {
        DatabaseLaundryRunRepository(
            AppDatabase.getDatabase(context)
                .laundryRunDao(),
        )
    }
    override val laundryRunItemRepository: LaundryRunItemRepository by lazy {
        DatabaseLaundryRunItemRepository(
            AppDatabase.getDatabase(context)
                .laundryRunItemDao(),
        )
    }
    override val userPreferencesRepository: DataStoreUserPreferencesRepository by lazy {
        DataStoreUserPreferencesRepository(dataStore)
    }
}
