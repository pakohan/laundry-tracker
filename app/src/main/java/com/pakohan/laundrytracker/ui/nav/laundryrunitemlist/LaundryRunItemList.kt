package com.pakohan.laundrytracker.ui.nav.laundryrunitemlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.data.entity.EnrichedLaundryRunItem
import com.pakohan.laundrytracker.ui.LaundryRunItemListViewModelFactory
import com.pakohan.laundrytracker.ui.nav.NavigationDestination
import com.pakohan.laundrytracker.ui.nav.laundryitemlist.LaundryItemListDestination
import com.pakohan.laundrytracker.ui.partials.ClickableText
import com.pakohan.laundrytracker.ui.partials.Link
import com.pakohan.laundrytracker.ui.partials.LinkData
import com.pakohan.laundrytracker.ui.partials.PlaceHolder

object LaundryRunItemListDestination : NavigationDestination {
    private const val BASE_PATH = "laundry_run_items"
    const val LAUNDRY_RUN_ID = "laundryRunId"

    override val route = "$BASE_PATH/{$LAUNDRY_RUN_ID}"
    override val titleRes = R.string.add_laundry_to_wash
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(LAUNDRY_RUN_ID) { type = NavType.IntType },
    )

    fun buildRoute(id: Int): String = "$BASE_PATH/${id}"
}

@Composable
fun LaundryRunItemList(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    laundryRunId: Int,
    viewModel: LaundryRunItemListViewModel = viewModel(factory = LaundryRunItemListViewModelFactory(laundryRunId)),
) {
    val laundryRunItems by viewModel.laundryRunItems.collectAsState()
    val laundryRun by viewModel.laundryRun.collectAsState()

    if (laundryRunItems.isEmpty()) {
        PlaceHolder(modifier = modifier) {
            ClickableText(
                linkData = LinkData(
                    fullText = """You don't have any fashion items yet.
Navigate to the list and add your first clothes.""".trimMargin(),
                    linksList = listOf(
                        Link(
                            linkText = "Navigate to the list",
                            linkInfo = "Add clothes to your closet",
                            onClick = {
                                navController.navigate(LaundryItemListDestination.route) {
                                    popUpTo(0)
                                }
                            },
                        ),
                    ),
                ),
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            items(laundryRunItems) {
                if (laundryRun?.startedAt == null) {
                    AddLaundryRunItemListItem(
                        entry = it,
                    ) { delta ->
                        viewModel.increment(
                            it,
                            delta,
                        )
                    }
                } else {
                    CheckLaundryRunItemListItem(entry = it) { delta ->
                        viewModel.incrementRetrieved(
                            it,
                            delta,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddLaundryRunItemListItem(
    modifier: Modifier = Modifier,
    entry: EnrichedLaundryRunItem,
    onIncrement: (Int) -> Unit,
) {
    ListItem(
        modifier = modifier.semantics {
            this.contentDescription = "laundry item ${entry.laundryItemName}"
        },
        headlineContent = {
            Row {
                Text(
                    text = entry.laundryItemName,
                    modifier = Modifier.weight(1f),
                )
                Text("${entry.quantity}")
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        onIncrement(1)
                    },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "increase amount of this item in washing machine",
                    )
                }
                IconButton(
                    enabled = entry.quantity > 0,
                    onClick = {
                        onIncrement(-1)
                    },
                ) {
                    Icon(
                        Icons.Filled.Remove,
                        contentDescription = "decrease amount of this item in washing machine",
                    )
                }
            }
        },
    )
}

@Composable
fun CheckLaundryRunItemListItem(
    modifier: Modifier = Modifier,
    entry: EnrichedLaundryRunItem,
    onIncrement: (Int) -> Unit,
) {
    ListItem(
        modifier = modifier.semantics {
            this.contentDescription = "laundry item ${entry.laundryItemName}"
        },
        headlineContent = {
            Row {
                Text(
                    text = entry.laundryItemName,
                    modifier = Modifier.weight(1f),
                )
                Text("${entry.retrievedQuantity} / ${entry.quantity}")
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(visible = entry.retrievedQuantity == entry.quantity) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "each item of this category got retrieved",
                        modifier = Modifier.padding(16.dp),
                    )
                }
                IconButton(
                    enabled = entry.retrievedQuantity < entry.quantity,
                    onClick = {
                        onIncrement(1)
                    },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "confirm another entity of this particular laundry item",
                    )
                }
                IconButton(
                    enabled = entry.retrievedQuantity > 0,
                    onClick = {
                        onIncrement(-1)
                    },
                ) {
                    Icon(
                        Icons.Filled.Remove,
                        contentDescription = "revert confirmation of another entity of this particular laundry item",
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun LaundryRunItemListItemPreview() {
    AddLaundryRunItemListItem(
        entry = EnrichedLaundryRunItem(
            laundryRunId = 1,
            laundryItemId = 1,
            laundryItemName = "T-Shirt",
            quantity = 2,
            retrievedQuantity = 1,
        ),
        onIncrement = { },
    )
}

@Preview
@Composable
fun CheckLaundryRunItemListItemPreview() {
    CheckLaundryRunItemListItem(
        entry = EnrichedLaundryRunItem(
            laundryRunId = 1,
            laundryItemId = 1,
            laundryItemName = "T-Shirt",
            quantity = 2,
            retrievedQuantity = 2,
        ),
        onIncrement = { },
    )
}
