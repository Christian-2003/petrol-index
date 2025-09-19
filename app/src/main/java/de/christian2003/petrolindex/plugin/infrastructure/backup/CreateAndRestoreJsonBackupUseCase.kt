package de.christian2003.petrolindex.plugin.infrastructure.backup

import android.content.Context
import android.util.Log
import de.christian2003.petrolindex.application.backup.BackupRepository
import de.christian2003.petrolindex.application.backup.CreateBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreBackupUseCase
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v1.PetrolEntryDto
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2.BackupConsumptionDto
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2.BackupMetadataDto
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2.BackupRootDto
import de.christian2003.petrolindex.plugin.infrastructure.backup.mapper.ConsumptionBackupMapper
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.time.LocalDateTime


/**
 * Implementation of the backup use case that generates a JSON backup.
 *
 * @param repository    Repository used to access the data.
 * @param context       Android context.
 */
class CreateAndRestoreJsonBackupUseCase(
    private val repository: BackupRepository,
    private val context: Context
): CreateBackupUseCase, RestoreBackupUseCase {

    /**
     * Mapper to use for mapping.
     */
    private val consumptionMapper: ConsumptionBackupMapper = ConsumptionBackupMapper()


    /**
     * Creates the serialized data for a backup. If the backup data cannot be serialized, this
     * returns null.
     *
     * @return  Serialized backup data.
     */
    override suspend fun create(): String? {
        try {
            //Get list of consumption DTOs:
            val consumptions: List<Consumption> = repository.getAllConsumptions().first()
            val consumptionDtos: List<BackupConsumptionDto> = consumptions.map { consumption ->
                consumptionMapper.toDto(consumption)
            }

            //Create JSON model for backup:
            val backupModel = BackupRootDto(
                metadata = BackupMetadataDto(
                    created = LocalDateTime.now(),
                    appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName!!,
                    appName = context.applicationInfo.loadLabel(context.packageManager).toString(),
                    backupVersion = 2
                ),
                consumptions = consumptionDtos
            )

            //Encode model to JSON:
            return Json.encodeToString(backupModel)
        }
        catch (e: Exception) {
            Log.e("JSON Backup", e.message ?: "No error message provided")
            return null
        }
    }

    /**
     * Restores the backup. This can both restore V1 backups as well as V2 backups.
     *
     * @param serializedData    Serialized data to restore.
     * @param restoreStrategy   Strategy for restoring data.
     * @return                  Whether the data was restored successfully.
     */
    override suspend fun restore(serializedData: String, restoreStrategy: RestoreStrategy): Boolean {
        val v2BackupModel: BackupRootDto? = try {
            Json.decodeFromString<BackupRootDto>(serializedData)
        } catch (_: Exception) {
            null
        }

        if (v2BackupModel != null) {
            //V2 backup:
            try {
                val consumptions: List<Consumption> = v2BackupModel.consumptions.map { consumption ->
                    consumptionMapper.toDomain(consumption)
                }
                repository.restoreBackup(consumptions, restoreStrategy)
                Log.d("JSON Backup", "Restored V2 backup")
                return true
            } catch (_: Exception) {
                Log.d("JSON Backup", "Cannot restore V2 backup")
                return false
            }
        }
        else {
            val v1BackupModel: List<PetrolEntryDto>? = try {
                Json.decodeFromString<List<PetrolEntryDto>>(serializedData)
            } catch (e: Exception) {
                Log.d("JSON Backup", e.message ?: "No message provided")
                null
            }

            if (v1BackupModel != null) {
                //V1 backup:
                val consumptions: List<Consumption> = v1BackupModel.map { petrolEntry ->
                    consumptionMapper.toDomain(petrolEntry)
                }
                repository.restoreBackup(consumptions, restoreStrategy)
                Log.d("JSON Backup", "Restored V1 backup")
                return true
            }

            //Unknown backup:
            Log.d("JSON Backup", "Unknown backup")
            return false
        }
    }

}
