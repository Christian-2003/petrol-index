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

    /**
     * Use case to get a list of all consumptions.
     */
    private lateinit var getAllConsumptionsUseCase: GetAllConsumptionsUseCase

    /**
     * Use case to delete a consumption.
     */
    private lateinit var deleteConsumptionUseCase: DeleteConsumptionUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized = false

    /**
     * All consumptions.
     */
    lateinit var consumptions: Flow<List<Consumption>>

    /**
     * Consumption that is currently waiting for confirmation before it is deleted.
     */
    var consumptionToDelete: Consumption? by mutableStateOf(null)

    /**
     * Indicates if the screen is in multiselect state.
     */
    var isInMultiselectState: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog to confirm the deletion of multi-selected details is visible.
     */
    var isDeleteMultiselectDialogVisible: Boolean by mutableStateOf(false)

    /**
     * IDs of the consumptions that are multi-selected.
     */
    val selectedConsumptionIds: MutableSet<Uuid> = mutableStateSetOf()

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
     * Initializes a new view model.
     */
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


    /**
     * Deletes the consumption that is currently marked for deletion.
     */
    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        val consumption: Consumption? = consumptionToDelete
        consumptionToDelete = null
        if (consumption != null) {
            deleteConsumptionUseCase.deleteConsumption(consumption.id)
        }
    }


    /**
     * Selects all consumptions in multiselect state.
     *
     * @param consumptions  Consumptions to select.
     */
    fun selectAllConsumptions(consumptions: List<Consumption>) {
        selectedConsumptionIds.addAll(consumptions.map { it -> it.id })
    }


    /**
     * Toggles whether a consumption is selected.
     *
     * @param consumption   Consumption for which to toggle selection.
     * @param isSelected    Whether the consumption should be selected.
     */
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


    /**
     * Queries whether the specified consumption is selected.
     *
     * @param consumption   Consumption to query.
     * @return              Whether the queried consumption is selected.
     */
    fun isConsumptionSelected(consumption: Consumption): Boolean {
        return selectedConsumptionIds.contains(consumption.id)
    }


    /**
     * Starts the multiselect state.
     *
     * @param consumption   First consumption to select.
     */
    fun startMultiselect(consumption: Consumption) {
        selectedConsumptionIds.add(consumption.id)
        isInMultiselectState = true
    }


    /**
     * Dismisses the multiselect state.
     */
    fun dismissMultiselectState() {
        isInMultiselectState = false
        selectedConsumptionIds.clear()
    }


    /**
     * Dismisses the dialog through which to delete multi-selected consumptions. Pass null as arguments
     * to dismiss without deleting.
     *
     * @param selectedConsumptionIds    IDs of the consumptions to delete.
     * @param allConsumptions           List of all consumptions.
     */
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
