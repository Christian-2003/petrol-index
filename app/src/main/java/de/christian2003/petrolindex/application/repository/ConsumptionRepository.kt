package de.christian2003.petrolindex.application.repository

import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import kotlin.uuid.Uuid


/**
 * Repository through which to access the consumptions.
 */
interface ConsumptionRepository {

    /**
     * Returns a list containing all consumptions.
     *
     * @return  Flow containing a list of all consumptions.
     */
    fun getAllConsumptions(): Flow<List<Consumption>>


    /**
     * Returns all consumptions in between the specified start and end days.
     *
     * @param start First day of the time period.
     * @param end   Last day of the time period.
     * @return      List of all consumptions in the specified time period.
     */
    fun getConsumptionsForTimePeriod(start: LocalDate, end: LocalDate): Flow<List<Consumption>>


    /**
     * Returns a list containing the most recent consumptions.
     *
     * @return  Flow containing a list of the most recent consumptions.
     */
    fun getRecentConsumptions(): Flow<List<Consumption>>


    /**
     * Returns the consumption with the passed ID. If no consumption exists, null is returned.
     *
     * @param id    ID of the consumption to return.
     * @return      Consumption with the passed ID or null.
     */
    suspend fun getConsumptionById(id: Uuid): Consumption?


    /**
     * Creates the new consumption that is passed as argument.
     *
     * @param consumption   Consumption to create.
     */
    suspend fun createConsumption(consumption: Consumption)


    /**
     * Updates the existing consumption that is passed as argument.
     *
     * @param consumption   Consumption to update.
     */
    suspend fun updateConsumption(consumption: Consumption)


    /**
     * Deletes the consumption that is passed as argument.
     *
     * @param consumption   Consumption to delete.
     */
    suspend fun deleteConsumption(consumption: Consumption)


    /**
     * Deletes all consumptions.
     */
    suspend fun deleteAllConsumptions()

}
