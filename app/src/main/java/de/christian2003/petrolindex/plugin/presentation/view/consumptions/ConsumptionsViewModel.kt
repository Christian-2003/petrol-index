package de.christian2003.petrolindex.plugin.presentation.view.consumptions

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.usecases.DeleteConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetAllConsumptionsUseCase
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ListItemDisplayStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the view displaying all petrol entries.
 */
class ConsumptionsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var getAllConsumptionsUseCase: GetAllConsumptionsUseCase

    private lateinit var deleteConsumptionUseCase: DeleteConsumptionUseCase

    private var isInitialized = false

    lateinit var consumptions: Flow<List<Consumption>>

    var consumptionToDelete: Consumption? by mutableStateOf(null)

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


    fun init(
        getAllConsumptionsUseCase: GetAllConsumptionsUseCase,
        deleteConsumptionUseCase: DeleteConsumptionUseCase
    ) {
        if (isInitialized) {
            return
        }
        this.getAllConsumptionsUseCase = getAllConsumptionsUseCase
        this.deleteConsumptionUseCase = deleteConsumptionUseCase
        consumptions = getAllConsumptionsUseCase.getAllConsumptions()
        isInitialized = true
    }


    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        val consumption: Consumption? = consumptionToDelete
        consumptionToDelete = null
        if (consumption != null) {
            deleteConsumptionUseCase.deleteConsumption(consumption.id)
        }
    }

}
