package de.christian2003.petrolindex.application.backup


/**
 * Use case for restoring backups.
 */
interface RestoreBackupUseCase {

    /**
     * Restores the backup.
     *
     * @param serializedData    Serialized data to restore.
     * @param restoreStrategy   Strategy for restoring data.
     * @return                  Whether the data was restored successfully.
     */
    suspend fun restore(serializedData: String, restoreStrategy: RestoreStrategy): Boolean

}
