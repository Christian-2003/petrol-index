package de.christian2003.petrolindex.view.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the settings page.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class SettingsViewModel: ViewModel() {

    /**
     * Attribute stores the repository through which to access the database.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the function which writes the specified string to the file of the URI
     * passed.
     */
    private lateinit var writeToFile: (Uri, String) -> Boolean

    /**
     * Attribute stores the function which reads the file whose URI is specified.
     */
    private lateinit var readFromFile: (Uri) -> String?


    /**
     * Method is called before the view model is passed to the SettingsView and instantiates the
     * view model.
     */
    fun init(
        repository: PetrolIndexRepository,
        writeToFile: (Uri, String) -> Boolean,
        readFromFile: (Uri) -> String?
    ) {
        this.repository = repository
        this.writeToFile = writeToFile
        this.readFromFile = readFromFile
    }


    /**
     * Method exports the app data into the JSON file whose URI is passed as argument. Afterwards,
     * the callback specified is invoked.
     *
     * @param uri           URI of the JSON file to which to write the data.
     * @param onFinished    Callback to invoke once the file is written.
     */
    fun exportDataToJsonFile(uri: Uri, onFinished: (Boolean) -> Unit) = viewModelScope.launch {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val petrolEntries = repository.allPetrolEntries.first()
        val json = gson.toJson(petrolEntries)
        val result = writeToFile(uri, json)
        onFinished(result)
    }


    /**
     * Method restores the data from the JSON file whose URI is passed as argument. If the file
     * specified can be read successfully, all current data will be deleted. Afterwards, the callback
     * specified is invoked.
     *
     * @param uri           URI of the JSON file to restore.
     * @param onFinished    Callback to invoke once the data is restored.
     */
    fun restoreDataFromJsonFile(uri: Uri, onFinished: (Boolean) -> Unit) = viewModelScope.launch {
        val gson = Gson()
        var petrolEntries: Array<PetrolEntry>? = null
        val json = readFromFile(uri)
        var success = false
        if (json != null) {
            try {
                petrolEntries = gson.fromJson(json, Array<PetrolEntry>::class.java)
            }
            catch (e: Exception) {
                Log.e("Restore", "" + if (e.message != null) { e.message } else { "Unknown error" })
            }
            if (petrolEntries != null) {
                repository.deleteAllPetrolEntries()
                petrolEntries.forEach { petrolEntry ->
                    repository.insertPetrolEntry(petrolEntry)
                }
                success = true
            }
        }
        onFinished(success)
    }

}
