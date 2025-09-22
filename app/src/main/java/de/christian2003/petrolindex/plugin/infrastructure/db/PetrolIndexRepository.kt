package de.christian2003.petrolindex.plugin.infrastructure.db

import de.christian2003.petrolindex.application.repository.BackupRepository
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import de.christian2003.petrolindex.application.repository.AnalysisRepository
import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.db.dao.ConsumptionDao
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity
import de.christian2003.petrolindex.plugin.infrastructure.db.mapper.ConsumptionDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import kotlin.uuid.Uuid


/**
 * Class implements a repository through which to access the petrol index database.
 *
 * @param consumptionDao    DAO through which to access the consumptions.
 */
class PetrolIndexRepository(
    private val consumptionDao: ConsumptionDao
): ConsumptionRepository, BackupRepository, AnalysisRepository {

    /**
     * Mapper maps the domain model 'Consumption' to the database entity.
     */
    private val consumptionMapper: ConsumptionDbMapper = ConsumptionDbMapper()

    /**
     * Flow contains a list of all consumptions. Can be null until "getAllConsumptions" is called
     * the first time.
     */
    private var consumptions: Flow<List<Consumption>>? = null


    /**
     * Returns a list containing all consumptions.
     *
     * @return  Flow containing a list of all consumptions.
     */
    override fun getAllConsumptions(): Flow<List<Consumption>> {
        if (consumptions == null) {
            consumptions = consumptionDao.selectAllConsumptions().map { list ->
                list.map { consumption ->
                    val domain: Consumption = consumptionMapper.toDomain(consumption)
                    return@map domain
                }
            }
        }
        return consumptions!!
    }

    /**
     * Returns a list containing the most recent consumptions.
     *
     * @return  Flow containing a list of the most recent consumptions.
     */
    override fun getRecentConsumptions(): Flow<List<Consumption>> {
        val consumptions: Flow<List<Consumption>> = consumptionDao.selectRecentConsumptions().map { list ->
            list.map { consumption ->
                consumptionMapper.toDomain(consumption)
            }
        }
        return consumptions
    }


    /**
     * Returns the consumption with the passed ID. If no consumption exists, null is returned.
     *
     * @param id    ID of the consumption to return.
     * @return      Consumption with the passed ID or null.
     */
    override suspend fun getConsumptionById(id: Uuid): Consumption? {
        val consumption: ConsumptionEntity? = consumptionDao.selectById(id)
        return if (consumption != null) {
            consumptionMapper.toDomain(consumption)
        } else {
            null
        }
    }


    /**
     * Creates the new consumption that is passed as argument.
     *
     * @param consumption   Consumption to create.
     */
    override suspend fun createConsumption(consumption: Consumption) {
        val entity: ConsumptionEntity = consumptionMapper.toEntity(consumption)
        consumptionDao.insert(entity)
    }


    /**
     * Updates the existing consumption that is passed as argument.
     *
     * @param consumption   Consumption to update.
     */
    override suspend fun updateConsumption(consumption: Consumption) {
        val entity: ConsumptionEntity = consumptionMapper.toEntity(consumption)
        consumptionDao.update(entity)
    }


    /**
     * Deletes the consumption that is passed as argument.
     *
     * @param consumption   Consumption to delete.
     */
    override suspend fun deleteConsumption(consumption: Consumption) {
        val entity: ConsumptionEntity = consumptionMapper.toEntity(consumption)
        consumptionDao.delete(entity)
    }


    /**
     * Deletes all consumptions.
     */
    override suspend fun deleteAllConsumptions() {
        consumptionDao.deleteAll()
    }


    /**
     * Restores the backup data passed as argument based on the specified restore strategy.
     *
     * @param consumptions      List of consumptions to restore.
     * @param restoreStrategy   Strategy for restoring the data.
     */
    override suspend fun restoreBackup(
        consumptions: List<Consumption>,
        restoreStrategy: RestoreStrategy
    ) {
        val consumptionEntities: List<ConsumptionEntity> = consumptions.map {
            consumption -> consumptionMapper.toEntity(consumption)
        }

        when (restoreStrategy) {
            RestoreStrategy.DELETE_EXISTING_DATA -> {
                consumptionDao.deleteAll()
                consumptionDao.insertAllAndIgnoreConflicts(consumptionEntities)
            }
            RestoreStrategy.REPLACE_EXISTING_DATA -> {
                consumptionDao.upsertAll(consumptionEntities)
            }
            RestoreStrategy.IGNORE_EXISTING_DATA -> {
                consumptionDao.insertAllAndIgnoreConflicts(consumptionEntities)
            }
        }
    }


    /**
     * Returns all consumptions in between the specified start and end days.
     *
     * @param start First day of the time period.
     * @param end   Last day of the time period.
     * @return      List of all consumptions in the specified time period.
     */
    override fun getConsumptionsForTimePeriod(start: LocalDate, end: LocalDate): Flow<List<Consumption>> {
        val consumptions: Flow<List<Consumption>> = consumptionDao.selectAllConsumptionsInDateRange(start, end).map { list ->
            list.map { consumption ->
                consumptionMapper.toDomain(consumption)
            }
        }

        return consumptions
    }

}
