package de.christian2003.petrolindex.plugin.presentation.view.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.apps.GetAppsUseCase
import de.christian2003.petrolindex.application.backup.CreateBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import de.christian2003.petrolindex.domain.apps.AppItem
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ListItemDisplayStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import androidx.core.content.edit


/**
 * Class implements the view model for the settings page.
 */
class SettingsViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Use case to create backups.
     */
    private lateinit var createBackupUseCase: CreateBackupUseCase

    /**
     * Use case to restore backups.
     */
    private lateinit var restoreBackupUseCase: RestoreBackupUseCase

    /**
     * Whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Stores the HTTP client to use in the frontend for web requests.
     */
    lateinit var client: OkHttpClient

    /**
     * URI of the backup file from which to restore the backup.
     */
    var restoreUri: Uri? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to change the list item display style is visible.
     */
    var isListItemDisplayDialogVisible: Boolean by mutableStateOf(false)

    /**
     * List of apps to advertise to the user.
     */
    val apps: MutableList<AppItem> = mutableStateListOf()

    /**
     * Display style for list items.
     */
    var listItemDisplayStyle: ListItemDisplayStyle
        set(value) {
            application.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
                putInt("list_item_style", value.ordinal)
            }
        }
        get() {
            val ordinal = application.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("list_item_style", ListItemDisplayStyle.DEFAULT.ordinal)
            return ListItemDisplayStyle.entries[ordinal]
        }


    /**
     * Instantiates the view model.
     *
     * @param createBackupUseCase   Use case to create a backup.
     * @param restoreBackupUseCase  Use case to restore a backup.
     * @param getAppsUseCase        Use case to get a list of all apps.
     * @param client                Http client for web requests
     */
    fun init(
        createBackupUseCase: CreateBackupUseCase,
        restoreBackupUseCase: RestoreBackupUseCase,
        getAppsUseCase: GetAppsUseCase,
        client: OkHttpClient
    ) {
        if (isInitialized) {
            return
        }
        this.createBackupUseCase = createBackupUseCase
        this.restoreBackupUseCase = restoreBackupUseCase
        this.client = client
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            val apps: List<AppItem> = getAppsUseCase.getAllApps()
            this@SettingsViewModel.apps.clear()
            this@SettingsViewModel.apps.addAll(apps)
        }
    }


    /**
     * Method exports the app data into the JSON file whose URI is passed as argument. Afterwards,
     * the callback specified is invoked.
     *
     * @param uri           URI of the JSON file to which to write the data.
     * @param onFinished    Callback to invoke once the file is written.
     */
    fun createBackup(uri: Uri, onFinished: (Boolean) -> Unit) = viewModelScope.launch {
        val serialized: String? = createBackupUseCase.create()
        var success: Boolean = serialized != null
        if (serialized != null) {
            success = writeToFile(uri, serialized)
        }

        withContext(Dispatchers.Main) {
            onFinished(success)
        }
    }


    /**
     * Method restores the data from the JSON file whose URI is passed as argument. If the file
     * specified can be read successfully, all current data will be deleted. Afterwards, the callback
     * specified is invoked.
     *
     * @param uri               URI of the JSON file to restore.
     * @param restoreStrategy   Strategy for restoring data.
     * @param onFinished        Callback to invoke once the data is restored.
     */
    fun restoreBackup(uri: Uri?, restoreStrategy: RestoreStrategy, onFinished: (Boolean) -> Unit) = viewModelScope.launch {
        var success = false
        if (uri != null) {
            val serialized: String? = readFromFile(uri)
            success = serialized != null
            if (serialized != null) {
                success = restoreBackupUseCase.restore(serialized, restoreStrategy)
            }
        }

        withContext(Dispatchers.Main) {
            onFinished(success)
        }
    }


    /**
     * Writes contents to a file and returns whether the file contents were written successfully.
     *
     * @param uri       URI of the file to write.
     * @param content   Content to write to the file.
     * @return          Whether the file contents were written successfully.
     */
    private fun writeToFile(uri: Uri, content: String): Boolean {
        val context: Context = getApplication<Application>().applicationContext
        try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray())
                stream.flush()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            return false
        }
        return true
    }


    /**
     * Reads contents from a file and returns them as string. If the file contents cannot be read,
     * null is returned.
     *
     * @param uri   URI of the file to read.
     * @return      File content or null.
     */
    private fun readFromFile(uri: Uri): String? {
        val context: Context = getApplication<Application>().applicationContext
        var content: String? = null
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(1024)
                val stringBuilder = StringBuilder()
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    stringBuilder.append(String(buffer, 0, bytesRead))
                }
                content = stringBuilder.toString()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            content = null
        }
        return content
    }

}
