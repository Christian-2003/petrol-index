package de.christian2003.petrolindex.application.analysis.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Value object models an analysis data point.
 *
 * @param date  Date of the data point.
 * @param value Value of the data point.
 */
data class AnalysisDataPoint(
    val date: LocalDate,
    val value: Int
) {

    /**
     * Calculates the hash code for the data point.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = date.hashCode()
        hash = 31 * hash + value.hashCode()
        return hash
    }


    /**
     * Tests whether the passed object is identical to this data point.
     *
     * @param other Other object to test.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is AnalysisDataPoint
                && other.date == this.date
                && other.value == this.value
    }


    /**
     * Converts the data point to a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return "[Date: ${date.format(formatter)}] [Value: $value]"
    }

}
