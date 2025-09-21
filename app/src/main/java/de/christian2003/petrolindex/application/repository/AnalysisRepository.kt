package de.christian2003.petrolindex.application.repository

import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


/**
 * Repository allows access to the data that is required by the analysis.
 */
interface AnalysisRepository {

    /**
     * Returns all consumptions in between the specified start and end days.
     *
     * @param start First day of the time period.
     * @param end   Last day of the time period.
     * @return      List of all consumptions in the specified time period.
     */
    fun getConsumptionsForTimePeriod(start: LocalDate, end: LocalDate): Flow<List<Consumption>>

}
