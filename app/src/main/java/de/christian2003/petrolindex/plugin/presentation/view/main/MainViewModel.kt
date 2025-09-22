package de.christian2003.petrolindex.plugin.presentation.view.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.usecases.DeleteConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetAllConsumptionsUseCase
import de.christian2003.petrolindex.application.usecases.GetRecentConsumptionsUseCase
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity
import de.christian2003.petrolindex.plugin.infrastructure.db.PetrolIndexRepository
import de.christian2003.petrolindex.plugin.infrastructure.update.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the main view.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Attribute stores the update manager through which to test whether a new update of the app
     * is available.
     */
    private lateinit var updateManager: UpdateManager

    /**
     * Use case to delete a consumption.
     */
    private lateinit var deleteConsumptionUseCase: DeleteConsumptionUseCase

    /**
     * Stores whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Stores the most recent consumptions.
     */
    lateinit var recentConsumptions: Flow<List<Consumption>>

    /**
     * Attribute indicates whether an update for the app is available.
     */
    var isUpdateAvailable: Boolean by mutableStateOf(false)

    /**
     * Attribute stores whether the update message has been dismissed by the user.
     */
    var isUpdateMessageDismissed: Boolean by mutableStateOf(false)

    /**
     * Stores the consumption to delete.
     */
    var consumptionToDelete: Consumption? by mutableStateOf(null)


    /**
     * Method instantiates the view model.
     *
     * @param updateManager                 Update manager through which to check whether updates
     *                                      are available.
     * @param getRecentConsumptionsUseCase  Use case to get the most recent consumptions.
     * @param deleteConsumptionUseCase      Use case to delete a consumption.
     */
    fun init(
        updateManager: UpdateManager,
        getRecentConsumptionsUseCase: GetRecentConsumptionsUseCase,
        deleteConsumptionUseCase: DeleteConsumptionUseCase
    ) {
        if (isInitialized) {
            return
        }
        this.deleteConsumptionUseCase = deleteConsumptionUseCase
        this.updateManager = updateManager
        isUpdateAvailable = updateManager.isUpdateAvailable
        recentConsumptions = getRecentConsumptionsUseCase.getRecentConsumptions()
        isInitialized = true
    }


    /**
     * Method requests the download for the new app version.
     */
    fun requestDownload() {
        updateManager.requestDownload()
    }


    /**
     * Deletes the consumption stored in "consumptionToDelete".
     */
    fun deleteConsumption() = viewModelScope.launch(Dispatchers.IO) {
        val consumption: Consumption? = consumptionToDelete
        consumptionToDelete = null
        if (consumption != null) {
            deleteConsumptionUseCase.deleteConsumption(consumption.id)
        }
    }

}
