package de.christian2003.petrolindex.plugin.presentation.view.consumptions

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.usecases.DeleteConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetAllConsumptionsUseCase
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.ui.composables.ListItemDisplayStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


/**
 * Class implements the view model for the view displaying all petrol entries.
 */
class ConsumptionsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var getAllConsumptionsUseCase: GetAllConsumptionsUseCase

    private lateinit var deleteConsumptionUseCase: DeleteConsumptionUseCase

    private var isInitialized = false

    lateinit var consumptions: Flow<List<Consumption>>

    var consumptionToDelete: Consumption? by mutableStateOf(null)

    var isInMultiselectState: Boolean by mutableStateOf(false)

    var isDeleteMultiselectDialogVisible: Boolean by mutableStateOf(false)

    var selectedConsumptionIds: MutableSet<Uuid> = mutableStateSetOf()


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

    fun selectAllConsumptions(consumptions: List<Consumption>) {
        selectedConsumptionIds.addAll(consumptions.map { it -> it.id })
    }

    fun toggleConsumptionSelection(consumption: Consumption, isSelected: Boolean) {
        if (isSelected) {
            selectedConsumptionIds.add(consumption.id)
        }
        else {
            selectedConsumptionIds.remove(consumption.id)
            if (selectedConsumptionIds.isEmpty()) {
                dismissMultiselectState()
            }
        }
    }

    fun isConsumptionSelected(consumption: Consumption): Boolean {
        return selectedConsumptionIds.contains(consumption.id)
    }

    fun startMultiselect(consumption: Consumption) {
        selectedConsumptionIds.add(consumption.id)
        isInMultiselectState = true
    }

    fun dismissMultiselectState() {
        isInMultiselectState = false
        selectedConsumptionIds.clear()
    }


    fun dismissDeleteMultiselectDialog(selectedConsumptionIds: Set<Uuid>? = null, allConsumptions: List<Consumption>? = null) = viewModelScope.launch(Dispatchers.Default) {
        isDeleteMultiselectDialogVisible = false
        isInMultiselectState = false
        if (selectedConsumptionIds != null && allConsumptions != null) {
            selectedConsumptionIds.forEach { id ->
                deleteConsumptionUseCase.deleteConsumption(id)
            }
        }
        this@ConsumptionsViewModel.selectedConsumptionIds.clear()
    }

}
