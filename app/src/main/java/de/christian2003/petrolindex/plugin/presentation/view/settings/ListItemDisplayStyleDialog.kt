package de.christian2003.petrolindex.plugin.presentation.view.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ListItemDisplayStyle


@Composable
fun ListItemDisplayStyleDialog(
    listItemDisplayStyle: ListItemDisplayStyle,
    onDismiss: (ListItemDisplayStyle) -> Unit
) {
    var selectedListItemDisplayStyle by remember { mutableStateOf(listItemDisplayStyle) }

    Dialog(
        onDismissRequest = {
            onDismiss(selectedListItemDisplayStyle)
        }
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(
                            start = 24.dp,
                            top = 24.dp,
                            end = 24.dp,
                            bottom = 8.dp
                        )
                ) {
                    Image(
                        painter = when (selectedListItemDisplayStyle) {
                            ListItemDisplayStyle.DEFAULT -> painterResource(R.drawable.ic_list_default)
                            ListItemDisplayStyle.CLASSIC -> painterResource(R.drawable.ic_list_classic)
                            else -> painterResource(R.drawable.ic_list_default)
                        },
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                HorizontalDivider()

                Text(
                    text = stringResource(R.string.settings_customization_listDialogTitle),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    ListItemDisplayStyle.entries.forEach { style ->
                        SegmentedButton(
                            selected = style == selectedListItemDisplayStyle,
                            onClick = {
                                selectedListItemDisplayStyle = style
                            },
                            shape = RoundedCornerShape(
                                topStart = if (style.ordinal == 0) { 100.dp } else { 0.dp },
                                topEnd = if (style.ordinal == ListItemDisplayStyle.entries.size - 1) { 100.dp } else { 0.dp },
                                bottomStart = if (style.ordinal == 0) { 100.dp } else { 0.dp },
                                bottomEnd = if (style.ordinal == ListItemDisplayStyle.entries.size - 1) { 100.dp } else { 0.dp }
                            )
                        ) {
                            Text(
                                text = when (style) {
                                    ListItemDisplayStyle.DEFAULT -> stringResource(R.string.settings_customization_listItem_default)
                                    ListItemDisplayStyle.CLASSIC -> stringResource(R.string.settings_customization_listItem_classic)
                                    else -> stringResource(R.string.settings_customization_listItem_default)
                                }
                            )
                        }
                    }
                }

                TextButton(
                    onClick = {
                        onDismiss(selectedListItemDisplayStyle)
                    },
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp,
                            bottom = 24.dp,
                        )
                        .align(Alignment.End)
                ) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        }
    }
}
