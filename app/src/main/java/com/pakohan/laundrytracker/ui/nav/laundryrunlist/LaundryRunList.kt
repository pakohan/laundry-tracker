package com.pakohan.laundrytracker.ui.nav.laundryrunlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryRun
import com.pakohan.laundrytracker.ui.LaundryRunListViewModelFactory
import com.pakohan.laundrytracker.ui.nav.TabNavigationDestination
import com.pakohan.laundrytracker.ui.nav.laundryrunitemlist.LaundryRunItemListDestination
import com.pakohan.laundrytracker.ui.partials.PlaceHolder
import org.ocpsoft.prettytime.PrettyTime

object LaundryRunListDestination : TabNavigationDestination {
    override val route = "laundry_runs"
    override val icon: ImageVector = Icons.Filled.LocalLaundryService
    override val titleRes = R.string.laundry_runs
    override val arguments: List<NamedNavArgument> = emptyList()
}

@Composable
fun LaundryRunList(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: LaundryRunListViewModel = viewModel(factory = LaundryRunListViewModelFactory()),
) {
    val laundryItems by viewModel.laundryItems.collectAsState()
    if (laundryItems.isEmpty()) {
        PlaceHolder(modifier = modifier) {
            Text(
                text = """Start by adding your first laundry run.
Press + below.""",
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                laundryItems,
                key = { it.id },
            ) {
                val context = LocalContext.current
                ListItem(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(LaundryRunItemListDestination.buildRoute(it.id))
                        }
                        .semantics {
                            this.contentDescription = "Laundry run ${it.id}"
                        },
                    headlineContent = { HeadlineContent(it) },
                    supportingContent = { SupportingContent(it) },
                    trailingContent = {
                        Row {
                            if (it.startedAt == null && it.quantity > 0) {
                                IconButton(
                                    onClick = {
                                        viewModel.startRun(
                                            context,
                                            it,
                                        )
                                    },
                                ) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = "start laundry run",
                                    )
                                }
                            }
                            if (it.retrievedQuantity == it.quantity) {
                                IconButton(onClick = { viewModel.delete(it) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "delete laundry run",
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun SupportingContent(laundryRun: EnrichedLaundryRun) {
    if (laundryRun.startedAt == null) {
        Text("${laundryRun.quantity} items")
    } else {
        Text("retrieved ${laundryRun.retrievedQuantity} of ${laundryRun.quantity} items")
    }
}

@Composable
fun HeadlineContent(laundryRun: EnrichedLaundryRun) {
    if (laundryRun.startedAt == null) {
        Text("not started yet")
    } else {
        val p = PrettyTime()
        Text("started ${p.format(laundryRun.startedAt)}")
    }
}
