package de.christian2003.petrolindex.plugin.presentation.view.licenses

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.R
import kotlinx.coroutines.launch
import java.io.InputStream


/**
 * Class implements the view model for the view displaying all software used by the app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class LicensesViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Attribute stores a list of all licenses used.
     */
    var licenses: List<License> by mutableStateOf(emptyList())

    /**
     * Attribute stores whether the licenses are currently being loaded.
     */
    var isLoading: Boolean by mutableStateOf(false)

    /**
     * Attribute stores the name of the software whose license is currently displayed in a dialog.
     */
    var displayedSoftwareName: String? by mutableStateOf(null)

    /**
     * Attribute stores the text of the license currently displayed in a dialog.
     */
    var displayedLicenseText: String? by mutableStateOf(null)


    /**
     * Method initializes the view model.
     */
    fun init() {
        if (isInitialized) {
            return
        }
        loadLicenses()
        isInitialized = true
    }


    /**
     * Method loads all licenses.
     */
    private fun loadLicenses() = viewModelScope.launch {
        if (!isLoading && licenses.isEmpty()) {
            isLoading = true
            val csv = readLicensesFile()
            if (csv != null) {
                parseLicenseFile(csv)
            }
            isLoading = false
        }
    }


    /**
     * Method loads the license text for the specified license. This loads in another thread and is
     * stored to the variables "displayedLicenseName" and "displayedLicenseText".
     *
     * @param license   License whose text to load.
     */
    fun loadLicenseText(license: License) = viewModelScope.launch {
        val content: ByteArray
        try {
            val application = getApplication<Application>()
            val resources = application.resources
            val inputStream: InputStream = resources.openRawResource(resources.getIdentifier(license.licenseFile, "raw", application.packageName))
            content = ByteArray(inputStream.available())
            inputStream.read(content)
            inputStream.close()
            displayedSoftwareName = license.softwareName
            displayedLicenseText = String(content)
        }
        catch (_: Exception) {
            displayedSoftwareName = null
            displayedLicenseText = null
        }
    }


    /**
     * Method reads the file containing the list of all used software licenses.
     *
     * @return  Content of the file.
     */
    private fun readLicensesFile(): String? {
        val content: ByteArray
        try {
            val inputStream: InputStream = getApplication<Application>().resources.openRawResource(R.raw.licenses)
            content = ByteArray(inputStream.available())
            inputStream.read(content)
            inputStream.close()
        }
        catch (_: Exception) {
            return null
        }
        return String(content)
    }


    /**
     * Method parses the licenses file whose CSV is passed as argument. The result is written to the
     * list "licenses".
     *
     * @param csv   CSV to parse.
     */
    private fun parseLicenseFile(csv: String) {
        val list = mutableListOf<License>()
        val software: List<String> = csv.split("\n")
        software.forEach { s ->
            val attributes: List<String> = s.split(",")
            if (attributes.size == 2) {
                list.add(License(
                    softwareName = attributes[0],
                    licenseFile = attributes[1],
                ))
            }
        }
        licenses = list
    }

}
