package de.christian2003.petrolindex.plugin.presentation.view.consumption

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.plugin.presentation.ui.composables.TextInput
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * View displays the page through which the user can add or configure a new petrol entry.
 *
 * @param viewModel     View model for the screen.
 * @param onNavigateUp  Callback to invoke to navigate up the navigation stack.
 */
@Composable
fun ConsumptionScreen(
    viewModel: ConsumptionViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(if (viewModel.consumptionToEdit == null) { R.string.add_petrol_entry_title_add } else { R.string.add_petrol_entry_title_edit }),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
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
                consumptionDate = viewModel.consumptionDate,
                volume = viewModel.volume,
                totalPrice = viewModel.totalPrice,
                distanceTraveled = viewModel.distanceTraveled,
                description = viewModel.description,
                volumeErrorMessage = viewModel.volumeErrorMessage,
                totalPriceErrorMessage = viewModel.totalPriceErrorMessage,
                distanceTraveledErrorMessage = viewModel.distanceTraveledErrorMessage,
                onVolumeChange = {
                    viewModel.updateVolume(it)
                },
                onTotalPriceChange = {
                    viewModel.updateTotalPrice(it)
                },
                onDistanceTraveledChange = {
                    viewModel.updateDistanceTraveled(it)
                },
                onDescriptionChange = {
                    viewModel.description = it
                },
                onShowDatePicker = {
                    viewModel.showModalDatePickerDialog = true
                }
            )
            Button(
                onClick = {
                    viewModel.save()
                    onNavigateUp()
                },
                enabled = viewModel.isDataValid.value,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.space_vertical))
            ) {
                Text(
                    text = stringResource(
                        if (viewModel.consumptionToEdit == null) {
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
                selectedMillis = viewModel.consumptionDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                onDateSelected = {
                    if (it != null) {
                        viewModel.consumptionDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
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
 * @param consumptionDate               Date at which the petrol entry shall be done.
 * @param volume                        String representation of the volume (in L) consumed by the
 *                                      user.
 * @param totalPrice                    String representation of the total price (in EUR) charged.
 * @param distanceTraveled              String representation of the distance traveled.
 * @param description                   Description of the petrol entry.
 * @param volumeErrorMessage            Error message if the volume entered is invalid.
 * @param totalPriceErrorMessage        Error message if the total price entered is invalid.
 * @param distanceTraveledErrorMessage  Error message if the distance traveled entered is invalid.
 * @param onVolumeChange                Callback invoked when the volume is changed.
 * @param onTotalPriceChange            Callback invoked when the total price is changed.
 * @param onDistanceTraveledChange      Callback invoked when the distance traveled is changed.
 * @param onDescriptionChange           Callback invoked when the description is changed.
 * @param onShowDatePicker              Callback invoked when the modal date picker dialog shall be
 *                                      displayed.
 */
@Composable
fun InputSection(
    consumptionDate: LocalDate,
    volume: String,
    totalPrice: String,
    distanceTraveled: String?,
    description: String,
    volumeErrorMessage: String?,
    totalPriceErrorMessage: String?,
    distanceTraveledErrorMessage: String?,
    onVolumeChange: (String) -> Unit,
    onTotalPriceChange: (String) -> Unit,
    onDistanceTraveledChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onShowDatePicker: () -> Unit
) {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //Enter date:
        TextInput(
            value = consumptionDate.format(dateTimeFormatter),
            onValueChange = { /* Value cannot change through text field (only through modifier pointer) */ },
            label = stringResource(R.string.add_petrol_entry_label_epoch_second),
            prefixIcon = painterResource(R.drawable.ic_calendar),
            modifier = Modifier.pointerInput(consumptionDate) {
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
        TextInput(
            value = volume,
            onValueChange = {
                onVolumeChange(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_volume),
            prefixIcon = painterResource(R.drawable.ic_petrol),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_volume),
            errorMessage = volumeErrorMessage,
            visualTransformation = NumberFormatTransformation()
        )

        //Enter total price:
        TextInput(
            value = totalPrice,
            onValueChange = {
                onTotalPriceChange(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_total_price),
            prefixIcon = painterResource(R.drawable.ic_price),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_total_price),
            errorMessage = totalPriceErrorMessage,
            visualTransformation = NumberFormatTransformation()
        )

        //Enter distance traveled:
        TextInput(
            value = distanceTraveled ?: "",
            onValueChange = {
                onDistanceTraveledChange(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_distance_traveled),
            prefixIcon = painterResource(R.drawable.ic_distance),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            suffixLabel = stringResource(R.string.add_petrol_entry_suffix_distance_traveled),
            errorMessage = distanceTraveledErrorMessage,
            visualTransformation = NumberFormatTransformation()
        )

        //Enter description:
        TextInput(
            value = description,
            onValueChange = {
                onDescriptionChange(it)
            },
            label = stringResource(R.string.add_petrol_entry_label_description),
            prefixIcon = painterResource(R.drawable.ic_description)
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
                Text(stringResource(R.string.button_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
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
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical))
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = stringResource(R.string.add_petrol_entry_content_description_info),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.space_horizontal_between))
            )
            Text(
                text = stringResource(R.string.add_petrol_entry_info),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
