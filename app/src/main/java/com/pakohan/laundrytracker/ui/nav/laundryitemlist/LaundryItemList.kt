package com.pakohan.laundrytracker.ui.nav.laundryitemlist

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.ui.LaundryItemListViewModelFactory
import com.pakohan.laundrytracker.ui.nav.TabNavigationDestination

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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(laundryItems) {
            ListItem(
                headlineContent = { Text(it.name) },
                trailingContent = {
                    IconButton(onClick = { viewModel.delete(it) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    }
}
