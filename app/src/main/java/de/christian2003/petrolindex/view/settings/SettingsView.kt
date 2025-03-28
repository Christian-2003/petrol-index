package de.christian2003.petrolindex.view.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.petrolindex.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Composable displays the view containing all settings.
 *
 * @param viewModel             View model for the view.
 * @param onNavigateBack        Callback to invoke in order to navigate back.
 * @param onNavigateToLicenses  Callback to invoke in order to navigate to the licenses view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLicenses: () -> Unit
) {
    val context = LocalContext.current
    val importMessageSuccess = stringResource(R.string.settings_data_import_success)
    val importMessageError = stringResource(R.string.settings_data_import_error)
    val exportMessageSuccess = stringResource(R.string.settings_data_export_success)
    val exportMessageError = stringResource(R.string.settings_data_export_error)
    val exportFilename = stringResource(R.string.export_file_name)
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
                            painter = painterResource(R.drawable.ic_back),
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
            //Data:
            SettingsTitle(stringResource(R.string.settings_data), false)
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_export),
                info = stringResource(R.string.settings_data_export_info),
                onClick = {
                    val formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val filename = exportFilename.replace("{arg}", formattedDate)
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    intent.putExtra(Intent.EXTRA_TITLE, filename)
                    createExportIntentLauncher.launch(intent)
                },
                prefixIcon = painterResource(R.drawable.ic_export)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_import),
                info = stringResource(R.string.settings_data_import_info),
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    restoreExportIntentLauncher.launch(intent)
                },
                prefixIcon = painterResource(R.drawable.ic_import)
            )

            //About
            SettingsTitle(stringResource(R.string.settings_about))
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_licenses),
                info = stringResource(R.string.settings_about_licenses_info),
                onClick = {
                    onNavigateToLicenses()
                },
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_license)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_github),
                info = stringResource(R.string.settings_about_github_info),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Christian-2003/petrol-index"))
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_github)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_issues),
                info = stringResource(R.string.settings_about_issues_info),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Christian-2003/petrol-index/issues"))
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_bug)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_more),
                info = stringResource(R.string.settings_about_more_info),
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.setData(uri)
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_android)
            )

            VersionInfo()
        }
    }
}


/**
 * Composable displays a title for a group of related settings.
 *
 * @param title Title to display.
 */
@Composable
fun SettingsTitle(
    title: String,
    divider: Boolean = true
) {
    if (divider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
    Text(
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.space_horizontal) + dimensionResource(R.dimen.image_xs) + dimensionResource(R.dimen.space_horizontal_between),
            top = dimensionResource(R.dimen.space_vertical),
            end = dimensionResource(R.dimen.space_horizontal),
            bottom = dimensionResource(R.dimen.space_vertical_between)),
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
}


/**
 * Composable displays an item button.
 *
 * @param setting   Title for the setting.
 * @param info      Info for the setting.
 * @param onClick   Callback to invoke when the item button is clicked.
 */
@Composable
fun SettingsItemButton(
    setting: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    prefixIcon: Painter? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical_between),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.space_horizontal_between))
                    .size(dimensionResource(R.dimen.image_xs))
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setting,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (endIcon != null) {
                    Icon(
                        painter = endIcon,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.space_horizontal_small))
                            .size(dimensionResource(R.dimen.image_xxs))
                    )
                }
            }
            Text(
                text = info,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}


@Composable
fun VersionInfo() {
    val context = LocalContext.current
    val versionName: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        Text(
            text = stringResource(R.string.settings_info_version).replace("{arg}", versionName),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.settings_info_copyright).replace("{arg}", LocalDateTime.now().year.toString()),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Icon(
            painter = painterResource(R.drawable.ic_splash_branding),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = ""
        )
    }
}
