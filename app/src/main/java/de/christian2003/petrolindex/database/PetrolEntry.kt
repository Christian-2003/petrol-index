package de.christian2003.petrolindex.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt


/**
 * Class models an entry for petrol consumption.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
@Entity(tableName = "petrol_entries")
class PetrolEntry(

    /**
     * Attribute stores a unique ID for the entry.
     */
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,

    /**
     * Attribute stores the epoch second for the date at which the petrol was consumed.
     */
    @SerializedName("epochSecond")
    val epochSecond: Long,

    /**
     * Attribute stores the volume (in ml) of petrol that was consumed.
     */
    @SerializedName("volume")
    val volume: Int,

    /**
     * Attribute stores the total price (in cents) of petrol that was consumed.
     */
    @SerializedName("totalPrice")
    val totalPrice: Int,

    /**
     * Attribute stores a description for the entry.
     */
    @SerializedName("description")
    val description: String,

    /**
     * Attribute stores the distance traveled since the last time the user visited the petrol station.
     * The value null indicates that no value has been set.
     */
    @SerializedName("distanceTraveled")
    val distanceTraveled: Int? = null

) {

    /**
     * Method returns the price per liter in cents
     */
    fun getPricePerLiter(): Int {
        if (volume == 0) {
            return 0
        }
        return ((totalPrice.toDouble() / volume.toDouble()) * 100.0).roundToInt()
    }

}
