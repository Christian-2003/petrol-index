package de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2

import de.christian2003.petrolindex.plugin.infrastructure.backup.serializer.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.uuid.Uuid


/**
 * DTO for backups of a consumption.
 */
@Serializable
data class BackupConsumptionDto(

    /**
     * ID of the consumption.
     */
    @SerialName("id")
    val id: Uuid,

    /**
     * Volume of the consumption.
     */
    @SerialName("volume")
    val volume: Int,

    /**
     * Total price of the consumption.
     */
    @SerialName("totalPrice")
    val totalPrice: Int,

    /**
     * Date of the consumption.
     */
    @SerialName("date")
    @Serializable(with = LocalDateSerializer::class)
    val consumptionDate: LocalDate,

    /**
     * Description of the consumption.
     */
    @SerialName("description")
    val description: String,

    /**
     * Distance traveled of the consumption.
     */
    @SerialName("distanceTraveled")
    val distanceTraveled: Int?

)
