package de.christian2003.petrolindex.view.add_petrol_entry

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.model.utils.LocaleFormatter
import kotlinx.coroutines.launch


/**
 * View displays the page through which the user can add or configure a new petrol entry.
 *
 * @param viewModel         View model for the view.
 * @param onNavigateBack    Callback to invoke to navigate back.
 * @param id                ID of the petrol entry to edit. Pass null to create a new entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetrolEntryView(
    viewModel: AddPetrolEntryViewModel,
    onNavigateBack: () -> Unit,
    id: Int? = null
) {
    if (id != null) {
        viewModel.loadPetrolEntryToEdit(id)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(if (viewModel.editedPetrolEntry == null) { R.string.add_petrol_entry_title_add } else { R.string.add_petrol_entry_title_edit }),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.add_petrol_entry_content_description_go_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.space_horizontal))
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopCard()
            InputSection(
                epochSecond = viewModel.epochSecond,
                volume = viewModel.volume,
                totalPrice = viewModel.totalPrice,
                distanceTraveled = viewModel.distanceTraveled,
                description = viewModel.description,
                totalPriceValid = viewModel.totalPriceValid,
                volumeValid = viewModel.volumeValid,
                onEpochSecondChanged = {
                    viewModel.epochSecond = it
                },
                onVolumeChanged = {
                    viewModel.volume = it
                    viewModel.isVolumeValid()
                },
                onTotalPriceChanged = {
                    viewModel.totalPrice = it
                    viewModel.isTotalPriceValid()
                },
                onDistanceTraveledChanged = {
                    viewModel.distanceTraveled = it
                },
                onDescriptionChanged = {
                    viewModel.description = it
                },
                onShowDatePicker = {
                    viewModel.showModalDatePickerDialog = true
                }
            )
            Button(
                onClick = {
                    viewModel.insert()
                    onNavigateBack()
                },
                enabled = viewModel.volumeValid && viewModel.totalPriceValid && viewModel.volume.isNotEmpty() && viewModel.totalPrice.isNotEmpty(),
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.space_vertical))
            ) {
                Text(
                    text = stringResource(
                        if (viewModel.editedPetrolEntry == null) {
                            R.string.add_petrol_entry_button_add
                        } else {
                            R.string.add_petrol_entry_button_edit
                        }
                    ),
                )
            }
        }

        //Modal date picker dialog:
        if (viewModel.showModalDatePickerDialog) {
            DatePickerModal(
                selectedMillis = viewModel.epochSecond * 1000,
                onDateSelected = {
                    if (it != null) {
                        viewModel.epochSecond = it / 1000
                    }
                },
                onDismiss = {
                    viewModel.showModalDatePickerDialog = false
                }
            )
        }
    }
}


/**
 * View displays the input section of the page through which the user can enter all information
 * required.
 *
 * @param epochSecond               Epoch second at which the petrol entry shall be done.
 * @param volume                    String representation of the volume (in L) consumed by the user.
 * @param totalPrice                String representation of the total price (in EUR) charged.
 * @param distanceTraveled          String representation of the distance traveled.
 * @param description               Description of the petrol entry.
 * @param totalPriceValid           Whether the total price entered by the user is valid.
 * @param volumeValid               Whether the volume entered by the user is valid.
 * @param onEpochSecondChanged      Callback invoked when the epoch second is changed.
 * @param onVolumeChanged           Callback invoked when the volume is changed.
 * @param onTotalPriceChanged       Callback invoked when the total price is changed.
 * @param onDistanceTraveledChanged Callback invoked when the distance traveled is changed.
 * @param onDescriptionChanged      Callback invoked when the description is changed.
 * @param onShowDatePicker          Callback invoked when the modal date picker dialog shall be displayed.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputSection(
    epochSecond: Long,
    volume: String,
    totalPrice: String,
    distanceTraveled: String,
    description: String,
    totalPriceValid: Boolean,
    volumeValid: Boolean,
    onEpochSecondChanged: (Long) -> Unit,
    onVolumeChanged: (String) -> Unit,
    onTotalPriceChanged: (String) -> Unit,
    onDistanceTraveledChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onShowDatePicker: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val scope = rememberCoroutineScope()
        val bringIntoViewRequester = remember { BringIntoViewRequester() }

        //Enter date:
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextFieldPrefixIcon(R.drawable.ic_calendar)
            OutlinedTextField(
                value = LocaleFormatter.epochSecondToFormattedDate(epochSecond),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = stringResource(R.string.add_petrol_entry_content_description_calendar),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onValueChange = {
                    onEpochSecondChanged(it.toLong())
                },
                label = {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_label_epoch_second)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.space_vertical))
                    .pointerInput(epochSecond) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                onShowDatePicker()
                            }
                        }
                    }
            )
        }


        //Enter volume:
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextFieldPrefixIcon(R.drawable.ic_petrol)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "" + volume,
                    singleLine = true,
                    onValueChange = {
                        onVolumeChanged(it)
                    },
                    suffix = {
                        Text(
                            text = stringResource(R.string.add_petrol_entry_suffix_volume)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.add_petrol_entry_label_volume)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = !volumeValid,
                    trailingIcon = {
                        if (!volumeValid) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.space_vertical))
                        .onFocusEvent {
                            if (it.isFocused) {
                                scope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                )
                if (!volumeValid) {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_error_volume),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }
            }
        }


        //Enter total price:
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextFieldPrefixIcon(R.drawable.ic_price)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "" + totalPrice,
                    singleLine = true,
                    onValueChange = {
                        onTotalPriceChanged(it)
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.add_petrol_entry_label_total_price)
                        )
                    },
                    suffix = {
                        Text(
                            text = stringResource(R.string.add_petrol_entry_suffix_total_price)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = !totalPriceValid,
                    trailingIcon = {
                        if (!totalPriceValid) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.space_vertical))
                        .onFocusEvent {
                            if (it.isFocused) {
                                scope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                )
                if (!totalPriceValid) {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_error_total_price),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }
            }
        }


        //Enter distance traveled:
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextFieldPrefixIcon(R.drawable.ic_distance)
            OutlinedTextField(
                value = distanceTraveled,
                singleLine = true,
                onValueChange = {
                    onDistanceTraveledChanged(it)
                },
                label = {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_label_distance_traveled)
                    )
                },
                suffix = {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_suffix_distance_traveled)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.space_vertical))
                    .onFocusEvent {
                        if (it.isFocused) {
                            scope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
        }


        //Enter description:
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextFieldPrefixIcon(R.drawable.ic_description)
            OutlinedTextField(
                value = description,
                singleLine = true,
                onValueChange = {
                    onDescriptionChanged(it)
                },
                label = {
                    Text(
                        text = stringResource(R.string.add_petrol_entry_label_description)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.space_vertical))
                    .onFocusEvent {
                        if (it.isFocused) {
                            scope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
        }
    }
}


/**
 * View displays a modal date picker dialog through which the user can select a date.
 *
 * @param selectedMillis    Millisecond of the date that is selected once the dialog is opened.
 * @param onDateSelected    Callback invoked once the user selects a date.
 * @param onDismiss         Callback invoked once the user closes the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedMillis: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    datePickerState.selectedDateMillis = selectedMillis

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


/**
 * View displays the card at the top of the page, through which the user is informed about what to
 * do on the page.
 */
@Composable
fun TopCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.space_vertical)),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical))
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = stringResource(R.string.add_petrol_entry_content_description_info),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.space_horizontal_between))
            )
            Text(
                text = stringResource(R.string.add_petrol_entry_info),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}


/**
 * Composable displays a prefix icon at the start of a text field.
 *
 * @param drawableResource  Drawable resource for the image.
 */
@Composable
fun TextFieldPrefixIcon(
    drawableResource: Int
) {
    Icon(
        painter = painterResource(drawableResource),
        contentDescription = "",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            top = dimensionResource(R.dimen.space_vertical) + dimensionResource(R.dimen.padding_material_text_field_top),
            end = dimensionResource(R.dimen.space_horizontal)
        )
    )
}
