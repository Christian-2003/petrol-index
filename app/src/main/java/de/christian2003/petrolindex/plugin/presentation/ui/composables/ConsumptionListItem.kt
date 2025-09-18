package de.christian2003.petrolindex.plugin.presentation.ui.composables

import de.christian2003.petrolindex.application.services.CurrencyFormatterService
import de.christian2003.petrolindex.application.services.DateTimeFormatterService
import de.christian2003.petrolindex.domain.model.Consumption
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R


/**
 * Displays a single consumption as list item.
 *
 * @param consumption   Consumption to display.
 * @param onEdit        Callback invoked to edit the consumption.
 * @param onDelete      Callback invoked to delete the consumption.
 */
@Composable
fun ConsumptionListItem(
    consumption: Consumption,
    onEdit: (Consumption) -> Unit,
    onDelete: (Consumption) -> Unit
) {
    val currencyFormatter = CurrencyFormatterService()
    val dateTimeFormatter = DateTimeFormatterService()
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                FormattedText(
                    text = stringResource(R.string.format_totalPrice, currencyFormatter.format(consumption.totalPrice)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FormattedText(
                    text = dateTimeFormatter.format(consumption.consumptionDate),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Value(
                formattedValue = currencyFormatter.format(consumption.calculatePricePerLiter())
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(spring(Spring.DampingRatioMediumBouncy)),
            exit = shrinkVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeOut(spring(Spring.DampingRatioMediumBouncy))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = dimensionResource(R.dimen.padding_vertical))
                ) {
                    FormattedText(
                        text = stringResource(R.string.format_volume, currencyFormatter.format(consumption.volume)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        prefixIcon = painterResource(R.drawable.ic_petrol)
                    )
                    if (consumption.distanceTraveled != null) {
                        FormattedText(
                            text = stringResource(R.string.format_distanceTraveled, currencyFormatter.format(consumption.distanceTraveled!!)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            prefixIcon = painterResource(R.drawable.ic_distance)
                        )
                    }
                    if (consumption.description.isNotBlank()) {
                        FormattedText(
                            text = consumption.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            prefixIcon = painterResource(R.drawable.ic_description)
                        )
                    }
                }
                FilledIconButton(
                    onClick = {
                        onEdit(consumption)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = ""
                    )
                }
                FilledIconButton(
                    onClick = {
                        onDelete(consumption)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}


@Composable
private fun FormattedText(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    prefixIcon: Painter? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = color,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal) / 2)
                    .size(dimensionResource(R.dimen.image_xxs))
            )
        }
        Text(
            text = text,
            color = color,
            style = style
        )
    }
}


/**
 * Displays a value.
 *
 * @param formattedValue    Formatted value to display.
 */
@Composable
private fun Value(
    formattedValue: String
) {
    Text(
        text = stringResource(R.string.format_pricePerLiter, formattedValue),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLargeIncreased)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(
                vertical = 4.dp,
                horizontal = 12.dp
            )
    )
}
