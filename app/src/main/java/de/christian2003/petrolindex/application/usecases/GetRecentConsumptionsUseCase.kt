package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow


/**
 * Use case returns the most recent consumptions.
 *
 * @param repository    Repository to access the consumptions.
 */
class GetRecentConsumptionsUseCase(
    private val repository: ConsumptionRepository
) {

    /**
     * Returns a list of the most recent consumptions.
     *
     * @return  List of the most recent consumptions.
     */
    fun getRecentConsumptions(): Flow<List<Consumption>> {
        return repository.getRecentConsumptions()
    }

}
