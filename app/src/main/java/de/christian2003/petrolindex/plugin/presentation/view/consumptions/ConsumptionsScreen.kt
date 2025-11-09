package de.christian2003.petrolindex.plugin.presentation.view.consumptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ConsumptionListItem
import de.christian2003.petrolindex.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Headline
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ListItemDisplayStyle
import de.christian2003.petrolindex.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Tooltip
import kotlin.uuid.Uuid


/**
 * Composable displays a list of all consumptions.
 *
 * @param viewModel         View model for the screen.
 * @param onNavigateUp      Callback to navigate up the navigation stack.
 * @param onEditConsumption Callback to edit a consumption.
 */
@Composable
fun ConsumptionsScreen(
    viewModel: ConsumptionsViewModel,
    onNavigateUp: () -> Unit,
    onEditConsumption: (Uuid) -> Unit
) {
    val consumptions by viewModel.consumptions.collectAsState(emptyList())
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)

    BackHandler {
        if (viewModel.isInMultiselectState) {
            viewModel.dismissMultiselectState()
        }
        else {
            onNavigateUp()
        }
    }

    Scaffold(
        topBar = {
            if (viewModel.isInMultiselectState) {
                MultiselectAppBar(
                    selectedConsumptionsCount = viewModel.selectedConsumptionIds.size,
                    onSelectAll = {
                        viewModel.selectAllConsumptions(consumptions)
                    },
                    onDeleteSelected = {
                        viewModel.isDeleteMultiselectDialogVisible = true
                    },
                    onFinishMultiselect = {
                        viewModel.dismissMultiselectState()
                    }
                )
            }
            else {
                DefaultAppBar(
                    scrollBehavior = scrollBehavior,
                    onNavigateUp = onNavigateUp
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
        ) {
            ConsumptionsList(
                consumptions = consumptions,
                listItemDisplayStyle = viewModel.listItemDisplayStyle,
                isInMultiselectState = viewModel.isInMultiselectState,
                onDeleteConsumption = { consumption ->
                    viewModel.consumptionToDelete = consumption
                },
                onEditConsumption =  { consumption ->
                    onEditConsumption(consumption.id)
                },
                onBeginMultiselect = { consumption ->
                    viewModel.startMultiselect(consumption)
                },
                onToggleSelection = { consumption, isSelected ->
                    viewModel.toggleConsumptionSelection(consumption, isSelected)
                },
                isConsumptionSelected = { consumption ->
                    viewModel.isConsumptionSelected(consumption)
                },
                windowInsets = WindowInsets(
                    bottom = innerPadding.calculateBottomPadding()
                )
            )
        }

        NavigationBarProtection(
            color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
            windowInsets = WindowInsets(
                bottom = innerPadding.calculateBottomPadding()
            )
        )

        //Dialog to delete:
        val consumptionToDelete: Consumption? = viewModel.consumptionToDelete
        if (consumptionToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.consumptions_deleteText),
                onDismiss = {
                    viewModel.consumptionToDelete = null
                },
                onConfirm = {
                    viewModel.delete()
                }
            )
        }

        //Dialog to delete in multiselect state:
        if (viewModel.isDeleteMultiselectDialogVisible) {
            ConfirmDeleteDialog(
                text = pluralStringResource(R.plurals.consumptions_deleteMultiselectText, viewModel.selectedConsumptionIds.size, viewModel.selectedConsumptionIds.size),
                onDismiss = {
                    viewModel.dismissDeleteMultiselectDialog()
                },
                onConfirm = {
                    viewModel.dismissDeleteMultiselectDialog(viewModel.selectedConsumptionIds, consumptions)
                }
            )
        }
    }
}


@Composable
private fun DefaultAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.consumptions_title),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@Composable
private fun MultiselectAppBar(
    selectedConsumptionsCount: Int,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onFinishMultiselect: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = pluralStringResource(R.plurals.consumptions_titleMultiselect, selectedConsumptionsCount, selectedConsumptionsCount),
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            Tooltip(
                tooltip = stringResource(R.string.consumptions_tooltip_closeMultiselect),
                anchor = TooltipAnchorPosition.End
            ) {
                IconButton(
                    onClick = onFinishMultiselect
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cancel),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        actions = {
            Tooltip(
                tooltip = stringResource(R.string.consumptions_tooltip_selectAll),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = onSelectAll
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_selectall),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Tooltip(
                tooltip = stringResource(R.string.consumptions_tooltip_deleteSelected),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = onDeleteSelected
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}


/**
 * Composable displays a list of consumptions.
 *
 * @param consumptions          List of consumptions to display.
 * @param listItemDisplayStyle  Style for the list items.
 * @param onDeleteConsumption   Callback to delete a consumption.
 * @param onEditConsumption     Callback to edit a consumption.
 * @param windowInsets          Window insets of the entire screen.
 */
@Composable
private fun ConsumptionsList(
    consumptions: List<Consumption>,
    listItemDisplayStyle: ListItemDisplayStyle,
    isInMultiselectState: Boolean,
    onDeleteConsumption: (Consumption) -> Unit,
    onEditConsumption: (Consumption) -> Unit,
    onBeginMultiselect: (Consumption) -> Unit,
    onToggleSelection: (Consumption, Boolean) -> Unit,
    isConsumptionSelected: (Consumption) -> Boolean,
    windowInsets: WindowInsets
) {
    if (consumptions.isEmpty()) {
        EmptyPlaceholder(
            title = stringResource(R.string.consumptions_emptyPlaceholder_title),
            subtitle = stringResource(R.string.consumptions_emptyPlaceholder_subtitle),
            painter = painterResource(R.drawable.el_consumptions),
            modifier = Modifier.fillMaxSize()
        )
    }
    else {
        val groupedConsumptions = consumptions.groupBy { consumption ->
            consumption.consumptionDate.withDayOfYear(1)
        }
        LazyColumn {
            groupedConsumptions.forEach { (year, yearConsumptions) ->
                item {
                    Column {
                        HorizontalDivider()
                        Headline(
                            title = year.year.toString()
                        )
                    }
                }
                items(yearConsumptions) { consumption ->
                    ConsumptionListItem(
                        consumption = consumption,
                        displayStyle = listItemDisplayStyle,
                        onDelete = onDeleteConsumption,
                        onEdit = onEditConsumption,
                        isInMultiselectState = isInMultiselectState,
                        onBeginMultiselect = onBeginMultiselect,
                        onToggleSelection = onToggleSelection,
                        isConsumptionSelected = isConsumptionSelected
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier.windowInsetsBottomHeight(windowInsets)
                )
            }
        }
    }
}
