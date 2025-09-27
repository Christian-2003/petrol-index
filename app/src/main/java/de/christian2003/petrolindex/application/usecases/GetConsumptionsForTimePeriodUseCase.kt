package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


class GetConsumptionsForTimePeriodUseCase(
    private val repository: ConsumptionRepository
) {

    fun getConsumptionsForTimePeriod(start: LocalDate, end: LocalDate): Flow<List<Consumption>> {
        return repository.getConsumptionsForTimePeriod(start, end)
    }

}
