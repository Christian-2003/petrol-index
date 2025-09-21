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
 * @param formattedValue    Formatted value to display.
 */
@Composable
fun Value(
    formattedValue: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Text(
        text = stringResource(R.string.format_pricePerLiter, formattedValue),
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
