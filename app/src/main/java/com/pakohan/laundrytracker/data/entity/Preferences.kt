package com.pakohan.laundrytracker.data.entity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DEFAULT_TIMER_SECONDS = 60 * 60

data class UserPreferences(
    val enableTimer: Boolean = false,
    val timerDurationSeconds: Int = DEFAULT_TIMER_SECONDS,
)

class DataStoreUserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    val preferences: Flow<UserPreferences> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        } else {
            throw it
        }
    }
        .map {
            UserPreferences(
                it[ENABLE_TIMER] ?: false,
                it[TIMER_DURATION_SECONDS] ?: DEFAULT_TIMER_SECONDS,
            )
        }

    suspend fun setEnableTimer(enableTimer: Boolean) {
        dataStore.edit {
            it[ENABLE_TIMER] = enableTimer
            it[TIMER_DURATION_SECONDS] = DEFAULT_TIMER_SECONDS
        }
    }

    suspend fun setTimerDurationSeconds(timerDurationSeconds: Int) {
        dataStore.edit {
            it[TIMER_DURATION_SECONDS] = timerDurationSeconds
        }
    }

    suspend fun startTimer(
        context: Context,
    ) {
        val settings = preferences.first()
        if (!settings.enableTimer) {
            return
        }

        val intent = Intent(AlarmClock.ACTION_SET_TIMER).putExtra(
            AlarmClock.EXTRA_MESSAGE,
            "Laundry finished",
        )
            .putExtra(
                AlarmClock.EXTRA_LENGTH,
                settings.timerDurationSeconds,
            )
            .putExtra(
                AlarmClock.EXTRA_SKIP_UI,
                true,
            )
        startActivity(
            context,
            intent,
            Bundle(),
        )
    }

    private companion object {
        val ENABLE_TIMER = booleanPreferencesKey("enable_timer")
        val TIMER_DURATION_SECONDS = intPreferencesKey("timer_duration")
    }
}
