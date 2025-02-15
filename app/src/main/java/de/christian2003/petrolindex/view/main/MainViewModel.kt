package de.christian2003.petrolindex.view.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import de.christian2003.petrolindex.model.update.UpdateManager
import kotlinx.coroutines.flow.Flow


/**
 * Class implements the view model for the main view.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the update manager through which to test whether a new update of the app
     * is available.
     */
    private lateinit var updateManager: UpdateManager

    /**
     * Attribute stores the list of petrol entries.
     */
    lateinit var petrolEntries: Flow<List<PetrolEntry>>

    /**
     * Attribute indicates whether an update for the app is available.
     */
    var isUpdateAvailable: Boolean by mutableStateOf(false)

    /**
     * Attribute stores whether the update message has been dismissed by the user.
     */
    var isUpdateMessageDismissed: Boolean by mutableStateOf(false)


    /**
     * Method instantiates the view model.
     *
     * @param repository    Repository through which to access data.
     * @param updateManager Update manager through which to check whether updates are available.
     */
    fun init(repository: PetrolIndexRepository, updateManager: UpdateManager) {
        this.repository = repository
        this.updateManager = updateManager
        petrolEntries = repository.allPetrolEntries

        isUpdateAvailable = updateManager.isUpdateAvailable
    }


    /**
     * Method requests the download for the new app version.
     */
    fun requestDownload() {
        updateManager.requestDownload()
    }

}
