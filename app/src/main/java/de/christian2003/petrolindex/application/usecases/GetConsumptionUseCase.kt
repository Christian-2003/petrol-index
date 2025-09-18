package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlin.uuid.Uuid


/**
 * Use case to get a consumption by an ID.
 *
 * @param consumptionRepository Repository to access the consumptions.
 */
class GetConsumptionUseCase(
    private val consumptionRepository: ConsumptionRepository
) {

    /**
     * Returns the consumption whose ID is passed as argument. If no consumption with the ID
     * specified exists, null is returned.
     *
     * @param id    ID of the consumption to return.
     * @return      Consumption with the ID specified or null.
     */
    suspend fun getConsumption(id: Uuid): Consumption? {
        return consumptionRepository.getConsumptionById(id)
    }

}
