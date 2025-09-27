package de.christian2003.petrolindex.plugin.presentation.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.plugin.presentation.ui.composables.Headline
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import de.christian2003.petrolindex.domain.apps.AppItem
import okhttp3.OkHttpClient


/**
 * Composable displays the view containing all settings.
 *
 * @param viewModel                 View model for the view.
 * @param onNavigateUp              Callback invoked to navigate up the navigation stack.
 * @param onNavigateToLicenses      Callback to invoke in order to navigate to the licenses view.
 * @param onNavigateToHelp          Callback invoked to navigate to the help screen.
 * @param onNavigateToOnboarding    Callback invoked to navigate to the onboarding.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val importMessageSuccess = stringResource(R.string.settings_data_importSuccess)
    val importMessageError = stringResource(R.string.settings_data_importError)
    val exportMessageSuccess = stringResource(R.string.settings_data_exportSuccess)
    val exportMessageError = stringResource(R.string.settings_data_exportError)
    val exportFilename = stringResource(R.string.export_file_name)

    val createExportIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.createBackup(
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

    val restoreExportIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.restoreUri = result.data!!.data!!
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                .verticalScroll(rememberScrollState())
        ) {
            //General:
            GeneralSection()
            HorizontalDivider()

            //Customization:
            Headline(
                title = stringResource(R.string.settings_customization),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_customization_listTitle),
                info = stringResource(R.string.settings_customization_listInfo),
                onClick = {
                    viewModel.isListItemDisplayDialogVisible = true
                },
                prefixIcon = painterResource(R.drawable.ic_list)
            )
            HorizontalDivider()


            //Data:
            Headline(
                title = stringResource(R.string.settings_data),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_exportTitle),
                info = stringResource(R.string.settings_data_exportInfo),
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
                setting = stringResource(R.string.settings_data_importTitle),
                info = stringResource(R.string.settings_data_importInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    restoreExportIntentLauncher.launch(intent)
                },
                prefixIcon = painterResource(R.drawable.ic_import)
            )
            HorizontalDivider()

            //Help
            Headline(
                title = stringResource(R.string.settings_help),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_help_helpMessagesTitle),
                info = stringResource(R.string.settings_help_helpMessagesInfo),
                onClick = onNavigateToHelp,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_help_outlined)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_help_onboardingTitle),
                info = stringResource(R.string.settings_help_onboardingInfo),
                onClick = onNavigateToOnboarding,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_onboarding)
            )
            HorizontalDivider()

            //About
            Headline(
                title = stringResource(R.string.settings_about),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_licensesTitle),
                info = stringResource(R.string.settings_about_licensesInfo),
                onClick = onNavigateToLicenses,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_license)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_githubTitle),
                info = stringResource(R.string.settings_about_githubInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/petrol-index".toUri())
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_github)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_issuesTitle),
                info = stringResource(R.string.settings_about_issuesInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/petrol-index/issues".toUri())
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_bug)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_moreTitle),
                info = stringResource(R.string.settings_about_moreInfo),
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.setData(uri)
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_android)
            )

            //Apps:
            AppsSection(
                apps = viewModel.apps,
                client = viewModel.client,
                onAppClick = { app ->
                    val intent = Intent(Intent.ACTION_VIEW, app.url)
                    context.startActivity(intent)
                }
            )
        }

        if (viewModel.restoreUri != null) {
            RestoreBackupDialog(
                onDismiss = {
                    viewModel.restoreUri = null
                },
                onConfirm = { restoreStrategy ->
                    viewModel.restoreBackup(
                        viewModel.restoreUri,
                        restoreStrategy = restoreStrategy,
                        onFinished = { success ->
                            if (success) {
                                Toast.makeText(context, importMessageSuccess, Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(context, importMessageError, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    viewModel.restoreUri = null
                }
            )
        }
    }

    if (viewModel.isListItemDisplayDialogVisible) {
        ListItemDisplayStyleDialog(
            listItemDisplayStyle = viewModel.listItemDisplayStyle,
            onDismiss = { style ->
                viewModel.isListItemDisplayDialogVisible = false
                viewModel.listItemDisplayStyle = style
            }
        )
    }
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
                vertical = dimensionResource(R.dimen.padding_vertical),
                horizontal = dimensionResource(R.dimen.margin_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (endIcon != null) {
                    Icon(
                        painter = endIcon,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.padding_horizontal) / 2)
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


/**
 * Displays the general information which contains info about the app.
 */
@Composable
private fun GeneralSection() {
    val context: Context = LocalContext.current
    val version: String? = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(R.dimen.image_l))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
            if (version != null) {
                Text(
                    text = version,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = stringResource(R.string.settings_about_copyright, LocalDate.now().year.toString()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}


/**
 * Displays the list of other apps that the user might check out.
 *
 * @param apps          List of apps to display.
 * @param client        OkHttpClient to use for loading the SVG images.
 * @param onAppClick    Callback invoked once an app is clicked.
 */
@Composable
private fun AppsSection(
    apps: List<AppItem>,
    client: OkHttpClient,
    onAppClick: (AppItem) -> Unit
) {
    val imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current.applicationContext)
        .okHttpClient(client)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    AnimatedVisibility(apps.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider()
            Headline(
                title = stringResource(R.string.settings_apps),
                indentToPrefixIcon = true
            )
            apps.forEach { app ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppClick(app)
                        }
                        .padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(app.iconUrl)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_horizontal))
                            .size(dimensionResource(R.dimen.image_xs))
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = app.displayName,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_external),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(start = dimensionResource(R.dimen.padding_horizontal) / 2)
                                    .size(dimensionResource(R.dimen.image_xxs))
                            )
                        }
                        val scheme: String? = app.url.scheme
                        val host: String? = app.url.host
                        if (scheme != null && host != null) {
                            Text(
                                text = "$scheme://$host",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
