package de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO for the V1 backup petrol entry ("PetrolEntry" has now been renamed to "Consumption").
 */
@Serializable
data class PetrolEntryDto(

    /**
     * Integer-based ID of the petrol entry.
     */
    @SerialName("id")
    val id: Int = 0,

    /**
     * Epoch second of the day on which the petrol was consumed.
     */
    @SerialName("epochSecond")
    val epochSecond: Long = 0,

    /**
     * Volume of the petrol entry.
     */
    @SerialName("volume")
    val volume: Int = 0,

    /**
     * Total price of the petrol entry.
     */
    @SerialName("totalPrice")
    val totalPrice: Int = 0,

    /**
     * Description of the petrol entry.
     */
    @SerialName("description")
    val description: String = "",

    /**
     * Distance traveled.
     */
    @SerialName("distanceTraveled")
    val distanceTraveled: Int? = null

)
