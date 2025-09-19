package de.christian2003.petrolindex.application.backup

import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow


/**
 * Repository allows to restore data from a backup.
 */
interface BackupRepository {

    /**
     * Restores the backup data passed as argument based on the specified restore strategy.
     *
     * @param consumptions      List of consumptions to restore.
     * @param restoreStrategy   Strategy for restoring the data.
     */
    suspend fun restoreBackup(consumptions: List<Consumption>, restoreStrategy: RestoreStrategy)


    /**
     * Returns a list containing all consumptions.
     *
     * @return  Flow containing a list of all consumptions.
     */
    fun getAllConsumptions(): Flow<List<Consumption>>

}
