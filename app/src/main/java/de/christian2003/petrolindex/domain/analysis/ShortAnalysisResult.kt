package de.christian2003.petrolindex.domain.analysis


/**
 * Domain value object models the result for the shorter and less extensive analysis that is
 * performed for a quick overview over the data.
 *
 * @param averagePricePerLiter  Average price per liter.
 * @param totalVolume           Total volume consumed.
 * @param totalPrice            Total price payed.
 * @param totalDistanceTraveled Total distance traveled.
 */
data class ShortAnalysisResult(
    val averagePricePerLiter: Int,
    val totalVolume: Int,
    val totalPrice: Int,
    val totalDistanceTraveled: Int
) {

    /**
     * Initializes the value object.
     */
    init {
        require(averagePricePerLiter >= 0) { "Price per liter cannot be negative" }
        require(totalVolume >= 0) { "Volume cannot be negative" }
        require(totalPrice >= 0) { "Price cannot be negative" }
        require(totalDistanceTraveled >= 0) { "Distance traveled cannot be negative" }
    }


    /**
     * Generates the hash code for the value object.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = averagePricePerLiter.hashCode()
        hash = 31 * hash + totalVolume.hashCode()
        hash = 31 * hash + totalPrice.hashCode()
        hash = 31 * hash + totalDistanceTraveled.hashCode()
        return hash
    }


    /**
     * Tests whether the object passed is identical to this object.
     *
     * @param other Other object to test.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is ShortAnalysisResult
                && other.averagePricePerLiter == this.averagePricePerLiter
                && other.totalVolume == this.totalVolume
                && other.totalPrice == this.totalPrice
                && other.totalDistanceTraveled == this.totalDistanceTraveled
    }


    /**
     * Converts the value object to a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        return "[PricePerLiter: $averagePricePerLiter] [Volume: $totalVolume] [Price: $totalPrice] [DistanceTraveled: $totalDistanceTraveled]"
    }

}
