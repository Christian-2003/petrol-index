package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlin.uuid.Uuid


/**
 * Use case to delete a consumption.
 *
 * @param consumptionRepository Repository to access the consumptions.
 */
class DeleteConsumptionUseCase(
    private val consumptionRepository: ConsumptionRepository
) {

    /**
     * Deletes the consumption whose ID is passed as argument. If no consumption with the specified
     * ID exists, nothing happens.
     *
     * @param id    ID of the consumption to delete.
     */
    suspend fun deleteConsumption(
        id: Uuid
    ) {
        val consumption: Consumption? = consumptionRepository.getConsumptionById(id)
        if (consumption != null) {
            consumptionRepository.deleteConsumption(consumption)
        }
    }

}
