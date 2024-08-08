package com.pakohan.laundrytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pakohan.laundrytracker.ui.nav.MainScaffold
import com.pakohan.laundrytracker.ui.theme.LaundryTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaundryTrackerTheme {
                MainScaffold()
            }
        }
    }
}
