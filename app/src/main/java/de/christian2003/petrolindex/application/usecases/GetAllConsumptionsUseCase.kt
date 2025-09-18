package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow


/**
 * Use case to get a list of all consumptions.
 *
 * @param consumptionRepository Repository to access the consumptions.
 */
class GetAllConsumptionsUseCase(
    private val consumptionRepository: ConsumptionRepository
) {


    /**
     * Returns a flow that contains a list with all consumptions within the app.
     *
     * @return  List of all consumptions.
     */
    fun getAllConsumptions(): Flow<List<Consumption>> {
        return consumptionRepository.getAllConsumptions()
    }

}
