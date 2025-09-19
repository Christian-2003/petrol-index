package de.christian2003.petrolindex.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import kotlin.math.roundToInt
import kotlin.uuid.Uuid


/**
 * Database entity for consumptions.
 */
@Entity(tableName = "petrol_entries")
class ConsumptionEntity(

    /**
     * Attribute stores a unique ID for the entry.
     */
    @PrimaryKey
    @ColumnInfo("id")
    val id: Uuid = Uuid.random(),

    /**
     * Attribute stores the epoch second for the date at which the petrol was consumed.
     */
    @ColumnInfo("epochSecond")
    val epochSecond: LocalDate,

    /**
     * Attribute stores the volume (in ml) of petrol that was consumed.
     */
    @ColumnInfo("volume")
    val volume: Int,

    /**
     * Attribute stores the total price (in cents) of petrol that was consumed.
     */
    @ColumnInfo("totalPrice")
    val totalPrice: Int,

    /**
     * Attribute stores a description for the entry.
     */
    @ColumnInfo("description")
    val description: String,

    /**
     * Attribute stores the distance traveled since the last time the user visited the petrol station.
     * The value null indicates that no value has been set.
     */
    @ColumnInfo("distanceTraveled")
    val distanceTraveled: Int? = null

) {

    /**
     * Method returns the price per liter in cents
     */
    @Deprecated("To be removed once DB entity is replaced with domain entity")
    fun getPricePerLiter(): Int {
        if (volume == 0) {
            return 0
        }
        return ((totalPrice.toDouble() / volume.toDouble()) * 100.0).roundToInt()
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is ConsumptionEntity) && (other.id == this.id)
    }

}
