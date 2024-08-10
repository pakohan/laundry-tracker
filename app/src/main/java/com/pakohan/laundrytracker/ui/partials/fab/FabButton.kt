package com.pakohan.laundrytracker.ui.partials.fab

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pakohan.laundrytracker.ui.FabButtonViewModelFactory
import com.pakohan.laundrytracker.ui.partials.TextInputDialog

@Composable
fun LaundryFabButton(
    navController: NavHostController,
    viewModel: FabButtonViewModel = viewModel(factory = FabButtonViewModelFactory(navController)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box {
        val currentState = uiState
        if (currentState != null) {
            FloatingActionButton(onClick = { currentState.onClick(context) }) {
                Icon(
                    imageVector = currentState.icon,
                    contentDescription = null,
                )
            }

            if (currentState.showDialog) {
                TextInputDialog(
                    title = "Add item",
                    cancel = viewModel::hideDialog,
                    onConfirm = viewModel::addLaundryItem,
                )
            }
        }
    }
}
