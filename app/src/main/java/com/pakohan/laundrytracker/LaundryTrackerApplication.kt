package com.pakohan.laundrytracker

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pakohan.laundrytracker.data.AppContainer
import com.pakohan.laundrytracker.data.AppDataContainer

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME,
)

class LaundryTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(
            this,
            dataStore,
        )
    }
}
