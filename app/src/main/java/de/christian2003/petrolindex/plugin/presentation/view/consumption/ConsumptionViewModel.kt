package de.christian2003.petrolindex.plugin.presentation.view.consumption

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.usecases.CreateConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.UpdateConsumptionUseCase
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity
import de.christian2003.petrolindex.plugin.infrastructure.db.PetrolIndexRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.uuid.Uuid


/**
 * Class implements the view model for the screen through which to add or edit a consumption.
 */
class ConsumptionViewModel: ViewModel() {

    /**
     * Use case to update an existing consumption.
     */
    private lateinit var updateConsumptionUseCase: UpdateConsumptionUseCase

    /**
     * Use case to create a new consumption.
     */
    private lateinit var createConsumptionUseCase: CreateConsumptionUseCase

    /**
     * Use case to get a specific consumption by it's ID.
     */
    private lateinit var getConsumptionUseCase: GetConsumptionUseCase

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Consumption that is being edited. This is null if a new consumption is being created.
     */
    var consumptionToEdit: Consumption? = null
        private set

    /**
     * Date for the consumption.
     */
    var consumptionDate: LocalDate by mutableStateOf(LocalDate.now())

    /**
     * String-representation of the volume (in liters) for the consumption.
     */
    var volume: String by mutableStateOf("")

    /**
     * String-representation of the total price (in euros) for the consumption.
     */
    var totalPrice: String by mutableStateOf("")

    /**
     * String-representation of the distance traveled for the consumption. This is null if no value
     * has been entered by the user.
     */
    var distanceTraveled: String? by mutableStateOf(null)

    /**
     * Description for the consumption.
     */
    var description: String by mutableStateOf("")

    /**
     * Indicates whether the date picker dialog to edit the consumption date is currently visible.
     */
    var showModalDatePickerDialog: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the value in "volume" is valid.
     */
    val isVolumeValid: State<Boolean> = derivedStateOf {
        val parsedVolume: Int? = volume.toIntOrNull()
        return@derivedStateOf (parsedVolume != null) && (parsedVolume >= 0)
    }

    /**
     * Indicates whether the value in "totalPrice" is valid.
     */
    val isTotalPriceValid: State<Boolean> = derivedStateOf {
        val parsedTotalPrice: Int? = totalPrice.toIntOrNull()
        return@derivedStateOf (parsedTotalPrice != null) && (parsedTotalPrice >= 0)
    }


    /**
     * Initializes the view model.
     *
     * @param updateConsumptionUseCase  Use case to update an existing consumption.
     * @param createConsumptionUseCase  Use case to create a new consumption.
     * @param getConsumptionUseCase     Use case to get a consumption by it's ID.
     * @param id                        ID of the consumption to edit or null if a new consumption
     *                                  should be created.
     */
    fun init(
        updateConsumptionUseCase: UpdateConsumptionUseCase,
        createConsumptionUseCase: CreateConsumptionUseCase,
        getConsumptionUseCase: GetConsumptionUseCase,
        id: Uuid? = null
    ) {
        if (isInitialized) {
            return
        }
        this.updateConsumptionUseCase = updateConsumptionUseCase
        this.createConsumptionUseCase = createConsumptionUseCase
        this.getConsumptionUseCase = getConsumptionUseCase
        isInitialized = true
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val consumption: Consumption? = getConsumptionUseCase.getConsumption(id)
                consumptionToEdit = consumption
                if (consumption != null) {
                    consumptionDate = consumption.consumptionDate
                    volume = consumption.volume.toString()
                    totalPrice = consumption.totalPrice.toString()
                    distanceTraveled = consumption.distanceTraveled?.toString()
                    description = consumption.description
                }
            }
        }
    }


    /**
     * Saves the consumption by either creating a new consumption or updating an existing one.
     * If the data entered is not valid, nothing happens.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        val volume: Int? = this@ConsumptionViewModel.volume.toIntOrNull()
        val totalPrice: Int? = this@ConsumptionViewModel.totalPrice.toIntOrNull()
        val distanceTraveled: Int? = this@ConsumptionViewModel.distanceTraveled?.toIntOrNull()
        if (volume != null && totalPrice != null) {
            if (consumptionToEdit != null) {
                //Edit existing consumption:
                updateConsumptionUseCase.updateConsumption(
                    id = consumptionToEdit!!.id,
                    volume = volume,
                    totalPrice = totalPrice,
                    consumptionDate = consumptionDate,
                    distanceTraveled = distanceTraveled,
                    description = description
                )
            }
            else {
                //Create new consumption:
                createConsumptionUseCase.createConsumption(
                    volume = volume,
                    totalPrice = totalPrice,
                    consumptionDate = consumptionDate,
                    distanceTraveled = distanceTraveled,
                    description = description
                )
            }
        }
    }

}
