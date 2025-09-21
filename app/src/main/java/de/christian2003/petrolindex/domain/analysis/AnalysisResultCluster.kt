package de.christian2003.petrolindex.domain.analysis


/**
 * Value object clusters some analysis results that belong together.
 *
 * @param sumDiagram        Diagram of the values that are summarized by the precision.
 * @param cumulatedDiagram  Diagram of the cumulated values.
 * @param totalSum          Total sum of values.
 * @param totalAverage      Average value for the consumption.
 * @param precisionAverage  Precision average (e.g. monthly, quarterly or yearly average).
 * @param type              Type of data stored in this cluster.
 */
data class AnalysisResultCluster(
    val sumDiagram: AnalysisDiagram,
    val cumulatedDiagram: AnalysisDiagram,
    val totalSum: Double,
    val totalAverage: Double,
    val precisionAverage: Double,
    val type: AnalysisResultClusterType
) {

    /**
     * Initializes the value object.
     */
    init {
        require(totalSum >= 0) { "Total sum cannot be negative" }
        require(totalAverage >= 0) { "Total average cannot be negative" }
        require(precisionAverage >= 0) { "Precision average cannot be negative" }
    }


    /**
     * Calculates the hash code for the value object.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash = sumDiagram.hashCode()
        hash = 31 * hash + cumulatedDiagram.hashCode()
        hash = 31 * hash + totalSum.hashCode()
        hash = 31 * hash + totalAverage.hashCode()
        hash = 31 * hash + precisionAverage.hashCode()
        return hash
    }


    /**
     * Tests whether the object passed is identical to this instance.
     *
     * @param other Other object to test.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is AnalysisResultCluster
                && other.sumDiagram == this.sumDiagram
                && other.cumulatedDiagram == this.cumulatedDiagram
                && other.totalSum == this.totalSum
                && other.totalAverage == this.totalAverage
                && other.precisionAverage == this.precisionAverage
    }


    /**
     * Converts the value object to a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        return "[TotalSum: $totalSum] [TotalAverage: $totalAverage] [PrecisionAverage: $precisionAverage]"
    }

}
