package com.pakohan.laundrytracker.ui.nav.laundryitemlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.ui.LaundryItemListViewModelFactory
import com.pakohan.laundrytracker.ui.nav.TabNavigationDestination
import com.pakohan.laundrytracker.ui.partials.PlaceHolder
import com.pakohan.laundrytracker.ui.partials.TextInputDialog

object LaundryItemListDestination : TabNavigationDestination {
    override val route = "laundry_items"
    override val icon: ImageVector = Icons.Filled.Checkroom
    override val titleRes = R.string.closet
    override val arguments: List<NamedNavArgument> = emptyList()
}

@Composable
fun LaundryItemList(
    modifier: Modifier = Modifier,
    viewModel: LaundryItemListViewModel = viewModel(factory = LaundryItemListViewModelFactory()),
) {
    val laundryItems by viewModel.laundryItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (laundryItems.isEmpty()) {
        PlaceHolder(
            modifier = modifier,
        ) {
            Text(
                """Start by adding your first fashion item.
Press + below.""",
            )
        }
    } else {
        LaundryItemList(
            modifier = modifier,
            items = laundryItems,
            onClick = viewModel::showDialog,
            onDelete = {
                viewModel.delete(
                    LaundryItem(
                        id = it.id,
                        name = it.name,
                    ),
                )
            },
        )

        val laundryItem = uiState.laundryItem
        if (laundryItem != null) {
            TextInputDialog(
                title = "Edit item",
                inputText = laundryItem.name,
                cancel = viewModel::hideDialog,
                onConfirm = viewModel::editLaundryItem,
            )
        }
    }
}

@Composable
private fun LaundryItemList(
    modifier: Modifier = Modifier,
    items: List<EnrichedLaundryItem>,
    onClick: (EnrichedLaundryItem) -> Unit,
    onDelete: (EnrichedLaundryItem) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = items) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onClick(it)
                    }
                    .semantics {
                        this.contentDescription = "laundry item ${it.name}"
                    },
                headlineContent = { Text(it.name) },
                trailingContent = {
                    if (it.canBeDeleted) {
                        IconButton(
                            onClick = { onDelete(it) },
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "delete laundry item",
                            )
                        }
                    }
                },
            )
        }
    }
}
