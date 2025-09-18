package de.christian2003.petrolindex.plugin.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.christian2003.petrolindex.R


/**
 * Displays a headline.
 *
 * @param title                 Title of the headline.
 * @param modifier              Modifier.
 * @param isEyecatcherVisible   Whether an eyecatcher is displayed for the endIcon.
 * @param indentToPrefixIcon    Whether to indent the headline with the same start padding used with
 *                              a prefix icon on GenericTextButton.
 * @param endIcon               End icon for the headline.
 * @param onClick               Callback invoked once the headline is clicked.
 */
@Composable
fun Headline(
    title: String,
    modifier: Modifier = Modifier,
    isEyecatcherVisible: Boolean = false,
    indentToPrefixIcon: Boolean = false,
    endIcon: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) {
                onClick!!()
            }
            .padding(
                start = if (indentToPrefixIcon) {
                    dimensionResource(R.dimen.margin_horizontal) + dimensionResource(R.dimen.image_xs) + dimensionResource(R.dimen.padding_horizontal)
                } else {
                    dimensionResource(R.dimen.margin_horizontal)
                },
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (endIcon != null) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    painter = endIcon,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "",
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                )
                if (isEyecatcherVisible) {
                    Eyecatcher()
                }
            }
        }
    }
}
