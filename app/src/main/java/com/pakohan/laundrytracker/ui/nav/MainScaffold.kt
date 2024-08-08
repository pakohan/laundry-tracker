package com.pakohan.laundrytracker.ui.nav

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.ui.ScaffoldViewModelFactory
import com.pakohan.laundrytracker.ui.partials.fab.LaundryFabButton
import com.pakohan.laundrytracker.ui.tabs

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController(),
    viewModel: MainScaffoldViewModel = viewModel(
        factory = ScaffoldViewModelFactory(navController),
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(uiState.currentTitle),
                canNavigateBack = uiState.showBackNavigation,
                navigateUp = { navController.navigateUp() },
            )
        },
        bottomBar = {
            BottomBar(
                visible = !uiState.showBackNavigation,
                selectedTab = uiState.selectedTab,
                onClick = {
                    Log.d(
                        "MainScaffold",
                        "navigating to $it",
                    )
                    navController.navigate(it) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
        floatingActionButton = {
            LaundryFabButton(navController)
        },
    ) { innerPadding ->
        LaundryTrackerNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title) },
        navigationIcon = {
            AnimatedVisibility(
                visible = canNavigateBack,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally(),
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_content_description),
                    )
                }
            }
        },
    )
}

@Composable
fun BottomBar(
    visible: Boolean,
    selectedTab: NavigationDestination?,
    onClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        NavigationBar {
            tabs.forEach {
                NavigationBarItem(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(it.titleRes)) },
                    selected = it == selectedTab,
                    onClick = { onClick(it.route) },
                )
            }
        }
    }
}
