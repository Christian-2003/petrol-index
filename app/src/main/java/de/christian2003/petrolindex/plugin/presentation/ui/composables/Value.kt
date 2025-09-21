package de.christian2003.petrolindex.plugin.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.christian2003.petrolindex.R


/**
 * Displays a value in a pill.
 *
 * @param formattedValue        Formatted value to display.
 * @param valueTextResourceId   ID of the string-resource containing the placeholder text in which to
 *                              replace the formatted value.
 * @param textColor             Color for the text.
 * @param backgroundColor       Color for the background
 */
@Composable
fun Value(
    formattedValue: String,
    valueTextResourceId: Int = R.string.format_pricePerLiter,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Text(
        text = stringResource(valueTextResourceId, formattedValue),
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLargeIncreased)
            .background(backgroundColor)
            .padding(
                vertical = 4.dp,
                horizontal = 12.dp
            )
    )
}
