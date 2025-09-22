package de.christian2003.petrolindex.plugin.presentation.view.consumption

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import de.christian2003.petrolindex.plugin.presentation.ui.composables.HelpCard
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
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(if (viewModel.consumptionToEdit == null) { R.string.consumption_titleCreate } else { R.string.consumption_titleEdit }),
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
                            contentDescription = "",
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
                .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.consumption_help),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
            }
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
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
            ) {
                Text(
                    text = stringResource(R.string.button_save),
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
            label = stringResource(R.string.consumption_consumptionDate_label),
            prefixIcon = painterResource(R.drawable.ic_calendar),
            modifier = Modifier
                .pointerInput(consumptionDate) {
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
                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )

        //Enter volume:
        TextInput(
            value = volume,
            onValueChange = {
                onVolumeChange(it)
            },
            label = stringResource(R.string.consumption_volume_label),
            prefixIcon = painterResource(R.drawable.ic_petrol),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.consumption_volume_suffix),
            errorMessage = volumeErrorMessage,
            visualTransformation = NumberFormatTransformation(),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )

        //Enter total price:
        TextInput(
            value = totalPrice,
            onValueChange = {
                onTotalPriceChange(it)
            },
            label = stringResource(R.string.consumption_totalPrice_label),
            prefixIcon = painterResource(R.drawable.ic_price),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffixLabel = stringResource(R.string.consumption_totalPrice_suffix),
            errorMessage = totalPriceErrorMessage,
            visualTransformation = NumberFormatTransformation(),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )

        //Enter distance traveled:
        TextInput(
            value = distanceTraveled ?: "",
            onValueChange = {
                onDistanceTraveledChange(it)
            },
            label = stringResource(R.string.consumption_distanceTraveled_label),
            prefixIcon = painterResource(R.drawable.ic_distance),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            suffixLabel = stringResource(R.string.consumption_distanceTraveled_suffix),
            errorMessage = distanceTraveledErrorMessage,
            visualTransformation = NumberFormatTransformation(),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )

        //Enter description:
        TextInput(
            value = description,
            onValueChange = {
                onDescriptionChange(it)
            },
            label = stringResource(R.string.consumption_description_label),
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
private fun DatePickerModal(
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
