package de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2

import de.christian2003.petrolindex.plugin.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Metadata for the backup.
 */
@Serializable
data class BackupMetadataDto(

    /**
     * Timestamp at which the backup was created.
     */
    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime,

    /**
     * Version of the app (e.g. "1.1.3").
     */
    @SerialName("appVersion")
    val appVersion: String,

    /**
     * Name of the app which created the backup (e.g. "Petrol Index").
     */
    @SerialName("appName")
    val appName: String,

    /**
     * Version code for the backup.
     */
    @SerialName("version")
    val backupVersion: Int

)
