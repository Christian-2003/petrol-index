package de.christian2003.petrolindex.domain.model

import java.time.LocalDate
import kotlin.math.roundToInt
import kotlin.uuid.Uuid


/**
 * Domain entity models a petrol consumption.
 *
 * @param volume            Volume (in milliliters) consumed.
 * @param totalPrice        Price (in cents) charged.
 * @param consumptionDate   Date on which the petrol was consumed.
 * @param description       Optional description which describes the consumption.
 * @param distanceTraveled  Optional distance (in meters) traveled before consuming petrol.
 * @param id                ID of the petrol consumption.
 */
class Consumption(
    volume: Int,
    totalPrice: Int,
    consumptionDate: LocalDate,
    description: String = "",
    distanceTraveled: Int? = null,
    id: Uuid = Uuid.Companion.random()
) {

    /**
     * Volume (in milliliters) of petrol that was consumed. This value must be greater than or equal
     * to 0.
     */
    var volume: Int = volume
        set(value) {
            require(value >= 0) { "Consumption value cannot be less than 0" }
            field = value
        }

    /**
     * Total price (in cents) paid for the petrol. This value must be greater than or equal to 0.
     */
    var totalPrice: Int = totalPrice
        set(value) {
            require(value >= 0) { "Consumption price cannot be less than 0" }
            field = value
        }

    /**
     * Date on which the petrol was consumed.
     */
    var consumptionDate: LocalDate = consumptionDate

    /**
     * Description which describes the consumption.
     */
    var description: String = description
        set(value) {
            field = value.ifBlank { "" }
        }

    /**
     * Total distance traveled with the petrol consumed. This value can be null, if no distance
     * traveled is provided by the user. Otherwise, this value must be greater than or equal to 0.
     */
    var distanceTraveled: Int? = distanceTraveled
        set(value) {
            require(value == null || value >= 0) { "Consumption distance traveled cannot be less than 0" }
            field = value
        }

    /**
     * Unique ID of the petrol consumption.
     */
    var id: Uuid = id
        private set(value) {
            require(id != Uuid.Companion.NIL) { "Consumption ID cannot be NIL-Id" }
            field = value
        }


    /**
     * Initializes the consumption entity.
     */
    init {
        this.volume = volume
        this.totalPrice = totalPrice
        this.consumptionDate = consumptionDate
        this.description = description
        this.distanceTraveled = distanceTraveled
        this.id = id
    }


    /**
     * Calculates the price per liter for the petrol consumption.
     */
    fun calculatePricePerLiter(): Int {
        if (volume == 0) {
            return 0
        }
        return ((totalPrice.toDouble() / volume.toDouble()) * 100).roundToInt()
    }


    /**
     * Returns the hash code for the consumption entity. This is identical to the hash code of the ID.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is identical to this consumption entity, based on their IDs.
     *
     * @param other Other object to test for equality.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Consumption) && (other.id == this.id)
    }


    /**
     * Returns a string-representation of this consumption entity. This can be used for debugging.
     *
     * @return  String-representation of the entity.
     */
    override fun toString(): String {
        return "[$id] [Volume: $volume] [Total Price: $totalPrice]"
    }

}
