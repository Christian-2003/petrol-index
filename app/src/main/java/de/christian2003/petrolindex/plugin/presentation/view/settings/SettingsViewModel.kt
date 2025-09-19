package de.christian2003.petrolindex.plugin.presentation.view.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.backup.CreateBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Class implements the view model for the settings page.
 */
class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var createBackupUseCase: CreateBackupUseCase

    private lateinit var restoreBackupUseCase: RestoreBackupUseCase

    private var isInitialized: Boolean = false

    var importUri: Uri? by mutableStateOf(null)


    /**
     * Instantiates the view model.
     */
    fun init(
        createBackupUseCase: CreateBackupUseCase,
        restoreBackupUseCase: RestoreBackupUseCase
    ) {
        if (isInitialized) {
            return
        }
        this.createBackupUseCase = createBackupUseCase
        this.restoreBackupUseCase = restoreBackupUseCase
        isInitialized = true
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
