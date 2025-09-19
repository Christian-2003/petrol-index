package de.christian2003.petrolindex.application.backup


/**
 * Use case for creating a backup.
 */
interface CreateBackupUseCase {

    /**
     * Creates the serialized data for a backup. If the backup data cannot be serialized, this
     * returns null.
     *
     * @return  Serialized backup data.
     */
    suspend fun create(): String?

}
