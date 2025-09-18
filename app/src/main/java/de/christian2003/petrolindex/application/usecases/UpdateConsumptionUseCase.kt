package de.christian2003.petrolindex.application.usecases

import android.accessibilityservice.GestureDescription
import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import java.time.LocalDate
import kotlin.uuid.Uuid


/**
 * Use case to update an existing consumption.
 *
 * @param consumptionRepository Repository to access the consumptions.
 */
class UpdateConsumptionUseCase(
    private val consumptionRepository: ConsumptionRepository
) {

    /**
     * Updates the consumption whose ID is passed as argument. If no consumption with the specified
     * ID exists, nothing happens and no data will be updated.
     *
     * @param id                ID of the consumption to update.
     * @param volume            Volume (in milliliters) for the consumption.
     * @param totalPrice        Total price (in cents) for the consumption.
     * @param description       Description describing the consumption.
     * @param distanceTraveled  Optional distance traveled (in meters) for the consumption.
     */
    suspend fun updateConsumption(
        id: Uuid,
        volume: Int,
        totalPrice: Int,
        consumptionDate: LocalDate,
        description: String,
        distanceTraveled: Int? = null
    ) {
        val consumption: Consumption? = consumptionRepository.getConsumptionById(id)

        if (consumption != null) {
            consumption.volume = volume
            consumption.totalPrice = totalPrice
            consumption.consumptionDate = consumptionDate
            consumption.description = description
            consumption.distanceTraveled = distanceTraveled
            consumptionRepository.updateConsumption(consumption)
        }
    }

}
