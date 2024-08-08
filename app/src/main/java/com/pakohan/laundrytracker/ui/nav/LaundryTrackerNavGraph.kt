package com.pakohan.laundrytracker.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemList
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemList
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemListDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunlist.LaundryRunList
import com.pakohan.laundrytracker.ui.nav.laundryrunlist.LaundryRunListDestination
import com.pakohan.laundrytracker.ui.nav.preferences.Preferences
import com.pakohan.laundrytracker.ui.nav.preferences.PreferencesDestination

interface NavigationDestination {
    val route: String
    val titleRes: Int
    val arguments: List<NamedNavArgument>
}

interface TabNavigationDestination : NavigationDestination {
    val icon: ImageVector
}

@Composable
fun LaundryTrackerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = LaundryRunListDestination.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(route = LaundryItemListDestination.route) {
            LaundryItemList()
        }
        composable(route = LaundryRunListDestination.route) {
            LaundryRunList(navController = navController)
        }
        composable(route = PreferencesDestination.route) {
            Preferences()
        }
        composable(
            route = LaundryRunItemListDestination.route,
            arguments = LaundryRunItemListDestination.arguments,
        ) {
            LaundryRunItemList(
                laundryRunId = it.arguments?.getInt(LaundryRunItemListDestination.LAUNDRY_RUN_ID) ?: 0,
            )
        }
    }
}
