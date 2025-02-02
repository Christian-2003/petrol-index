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
        //Enter date:
        TextInput(
            value = LocaleFormatter.epochSecondToFormattedDate(epochSecond),
            onValueChanged = {
                onEpochSecondChanged(it.toLong())
            },
            label = stringResource(R.string.add_petrol_entry_label_epoch_second),
            prefixIcon = R.drawable.ic_calendar,
            trailingIcon = R.drawable.ic_calendar,
            modifier = Modifier.pointerInput(epochSecond) {
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

        //Enter volume:
        val volumeError: String? = if (!volumeValid) { stringResource(R.string.add_petrol_entry_error_volume) } else { null }
        TextInput(
            value = volume,
            onValueChanged = {
                onVolumeChanged(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_volume),
            prefixIcon = R.drawable.ic_petrol,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_volume),
            errorMessage = volumeError
        )

        //Enter total price:
        val totalPriceError: String? = if (!totalPriceValid) { stringResource(R.string.add_petrol_entry_error_total_price) } else { null }
        TextInput(
            value = totalPrice,
            onValueChanged = {
                onTotalPriceChanged(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_total_price),
            prefixIcon = R.drawable.ic_price,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_total_price),
            errorMessage = totalPriceError
        )

        //Enter distance traveled:
        TextInput(
            value = distanceTraveled,
            onValueChanged = {
                onDistanceTraveledChanged(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_distance_traveled),
            prefixIcon = R.drawable.ic_distance,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_distance_traveled)
        )

        //Enter description:
        TextInput(
            value = description,
            onValueChanged = {
                onDescriptionChanged(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_description),
            prefixIcon = R.drawable.ic_description
        )
    }
}


/**
 * Composable displays a versatile text input.
 *
 * @param value             Value for the text field.
 * @param onValueChanged    Callback invoked once the value changes.
 * @param label             Label text to display.
 * @param prefixIcon        Drawable resource for the prefix icon.
 * @param modifier          Modifier for the text field.
 * @param keyboardOptions   Keyboard options.
 * @param suffixLabel       Text to display for the suffix label.
 * @param trailingIcon      Drawable resource for the trailing icon.
 * @param errorMessage      Error message to display.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextInput(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    prefixIcon: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    suffixLabel: String? = null,
    trailingIcon: Int? = null,
    errorMessage: String? = null
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    //Text displayed as suffix:
    val suffixView: (@Composable () -> Unit)? = if (suffixLabel != null) {
        @Composable {
            Text(
                text = suffixLabel,
                color = if (errorMessage == null) { MaterialTheme.colorScheme.onSurfaceVariant } else { MaterialTheme.colorScheme.error }
            )
        }
    } else {
        null
    }

    //Trailing icon displayed if an error occurs:
    val trailingIconView: (@Composable () -> Unit)? = if (errorMessage != null) {
        @Composable {
            Icon(
                imageVector = Icons.Filled.Info,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = errorMessage
            )
        }
    } else if (trailingIcon != null) {
        @Composable {
            Icon(
                painter = painterResource(trailingIcon),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = ""
            )
        }
    } else {
        null
    }

    //Supporting text displayed if an error occurs:
    val supportingTextView: (@Composable () -> Unit)? = if (errorMessage != null) {
        @Composable {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        null
    }

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        TextFieldPrefixIcon(prefixIcon)
        OutlinedTextField(
            value = value,
            singleLine = true,
            onValueChange = onValueChanged,
            label = {
                Text(
                    text = label
                )
            },
            keyboardOptions = keyboardOptions,
            suffix = suffixView,
            isError = errorMessage != null,
            trailingIcon = trailingIconView,
            supportingText = supportingTextView,
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
