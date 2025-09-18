package de.christian2003.petrolindex.plugin.presentation.view.consumption

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.usecases.CreateConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.UpdateConsumptionUseCase
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.uuid.Uuid
import de.christian2003.petrolindex.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


/**
 * Class implements the view model for the screen through which to add or edit a consumption.
 */
class ConsumptionViewModel(application: Application): AndroidViewModel(application) {

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
     * Number format to use when parsing the values entered by the user.
     */
    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.getDefault())

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
        private set //Delegates cannot have custom setters. Therefore, use "updateVolume()"

    /**
     * String-representation of the total price (in euros) for the consumption.
     */
    var totalPrice: String by mutableStateOf("")
        private set //Delegates cannot have custom setters. Therefore, use "updateTotalPrice()"

    /**
     * String-representation of the distance traveled for the consumption. This is null if no value
     * has been entered by the user.
     */
    var distanceTraveled: String? by mutableStateOf(null)
        private set //Delegates cannot have custom setters. Therefore, use "updateDistanceTraveled()"

    /**
     * Description for the consumption.
     */
    var description: String by mutableStateOf("")

    /**
     * Indicates whether the date picker dialog to edit the consumption date is currently visible.
     */
    var showModalDatePickerDialog: Boolean by mutableStateOf(false)

    /**
     * Error message for the text field through which to enter the value.
     */
    var volumeErrorMessage: String? by mutableStateOf(null)

    /**
     * Error message for the text field through which to enter the total price.
     */
    var totalPriceErrorMessage: String? by mutableStateOf(null)


    /**
     * Error message for the text field through which to enter the distance traveled.
     */
    var distanceTraveledErrorMessage: String? by mutableStateOf(null)

    /**
     * Indicates whether all data is valid.
     */
    val isDataValid: State<Boolean> = derivedStateOf {
        val parsedVolume: Double? = try {
            numberFormat.parse(volume)!!.toDouble()
        } catch (_: Exception) {
            null
        }
        val parsedTotalPrice: Double? = try {
            numberFormat.parse(totalPrice)!!.toDouble()
        } catch (_: Exception) {
            null
        }
        return@derivedStateOf parsedVolume != null
                && parsedVolume >= 0
                && parsedTotalPrice != null
                && parsedTotalPrice >= 0
                && volumeErrorMessage == null
                && totalPriceErrorMessage == null
                && distanceTraveledErrorMessage == null
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
                    //Cannot use "numberFormat"-attribute because it puts group separator into the
                    //formatted string, which confuses the visual transformation for the TextField.
                    val roundingFormat: NumberFormat = DecimalFormat("#.##")
                    consumptionDate = consumption.consumptionDate
                    volume = roundingFormat.format(consumption.volume.toDouble() / 100.0)
                    totalPrice = roundingFormat.format(consumption.totalPrice.toDouble() / 100.0)
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
        val volume: Int? = try {
            (numberFormat.parse(this@ConsumptionViewModel.volume)!!.toDouble() * 100).roundToInt()
        } catch (_: Exception) {
            null
        }
        val totalPrice: Int? = try {
            (numberFormat.parse(this@ConsumptionViewModel.totalPrice)!!.toDouble() * 100).roundToInt()
        } catch (_: Exception) {
            null
        }
        val distanceTraveled: Int? = try {
            numberFormat.parse(this@ConsumptionViewModel.distanceTraveled!!)!!.toInt()
        } catch (_: Exception) {
            null
        }
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


    /**
     * Updates the volume.
     *
     * @param value New volume.
     */
    fun updateVolume(value: String) {
        volume = value

        if (value.isBlank()) {
            volumeErrorMessage = getApplication<Application>().getString(R.string.error_emptyText)
            return
        }
        try {
            numberFormat.parse(value)!!.toDouble()
            volumeErrorMessage = null
        }
        catch (_: Exception) {
            volumeErrorMessage = getApplication<Application>().getString(R.string.error_volumeError)
        }
    }


    /**
     * Updates the total price.
     *
     * @param value New total price
     */
    fun updateTotalPrice(value: String) {
        totalPrice = value

        if (value.isBlank()) {
            totalPriceErrorMessage = getApplication<Application>().getString(R.string.error_emptyText)
            return
        }
        try {
            numberFormat.parse(value)!!.toDouble()
            totalPriceErrorMessage = null
        }
        catch (_: Exception) {
            totalPriceErrorMessage = getApplication<Application>().getString(R.string.error_totalPriceError)
        }
    }


    /**
     * Updates the distance traveled.
     *
     * @param value New distance traveled.
     */
    fun updateDistanceTraveled(value: String) {
        distanceTraveled = value

        if (value.isBlank()) {
            distanceTraveledErrorMessage = null
            return
        }
        try {
            value.toInt()
            distanceTraveledErrorMessage = null
        }
        catch (_: Exception) {
            distanceTraveledErrorMessage = getApplication<Application>().getString(R.string.error_totalPriceError)
        }
    }

}
