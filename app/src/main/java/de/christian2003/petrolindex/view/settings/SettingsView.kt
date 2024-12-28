package de.christian2003.petrolindex.view.settings

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import de.christian2003.petrolindex.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val importMessageSuccess = stringResource(R.string.settings_data_import_success)
    val importMessageError = stringResource(R.string.settings_data_import_error)
    val exportMessageSuccess = stringResource(R.string.settings_data_export_success)
    val exportMessageError = stringResource(R.string.settings_data_export_error)
    //Activity result launcher to select a file for the export:
    val createExportIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.exportDataToJsonFile(
                uri = result.data!!.data!!,
                onFinished = { r ->
                    if (r) {
                        Toast.makeText(context, exportMessageSuccess, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, exportMessageError, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
    //Activity result launcher to select a file from which to restore data:
    val restoreExportIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.restoreDataFromJsonFile(
                uri = result.data!!.data!!,
                onFinished = { r ->
                    if (r) {
                        Toast.makeText(context, importMessageSuccess, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, importMessageError, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.settings_content_description_go_back),
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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsTitle(
                title = stringResource(R.string.settings_data)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_export),
                info = stringResource(R.string.settings_data_export_info),
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    createExportIntentLauncher.launch(intent)
                }
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_import),
                info = stringResource(R.string.settings_data_import_info),
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    restoreExportIntentLauncher.launch(intent)
                }
            )
        }
    }
}


@Composable
fun SettingsTitle(
    title: String
) {
    Text(
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.space_horizontal),
            top = dimensionResource(R.dimen.space_vertical),
            end = dimensionResource(R.dimen.space_horizontal),
            bottom = dimensionResource(R.dimen.space_vertical_between)),
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyLarge
    )
}


@Composable
fun SettingsItemButton(
    setting: String,
    info: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical_between),
                horizontal = dimensionResource(R.dimen.space_horizontal))
    ) {
        Text(
            text = setting,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = info,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
