package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import java.time.LocalDate


/**
 * Use case to create a new consumption.
 *
 * @param consumptionRepository Repository to access the consumptions.
 */
class CreateConsumptionUseCase(
    private val consumptionRepository: ConsumptionRepository
) {

    /**
     * Creates a new consumption.
     *
     * @param volume            Volume (in milliliters) for the consumption.
     * @param totalPrice        Total price (in cents) for the consumption.
     * @param consumptionDate   Date on which the petrol was consumed.
     * @param description       Description for the consumption.
     * @param distanceTraveled  Optional distance traveled (in meters) for the consumption.
     */
    suspend fun createConsumption(
        volume: Int,
        totalPrice: Int,
        consumptionDate: LocalDate,
        description: String,
        distanceTraveled: Int? = null
    ) {
        val consumption = Consumption(
            volume = volume,
            totalPrice = totalPrice,
            consumptionDate = consumptionDate,
            description = description,
            distanceTraveled = distanceTraveled
        )

        consumptionRepository.createConsumption(consumption)
    }

}
