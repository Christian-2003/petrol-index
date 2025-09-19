package de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root object for the JSON backup.
 */
@Serializable
data class BackupRootDto(

    /**
     * Metadata of the backup.
     */
    @SerialName("metadata")
    val metadata: BackupMetadataDto,

    /**
     * List of consumptions.
     */
    @SerialName("consumptions")
    val consumptions: List<BackupConsumptionDto>

)
