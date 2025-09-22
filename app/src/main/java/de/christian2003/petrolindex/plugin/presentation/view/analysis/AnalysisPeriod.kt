package de.christian2003.petrolindex.plugin.presentation.view.analysis

import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Analysis period can be selected by the user to define a date range in which consumptions should
 * be analyzed.
 *
 * @param start First day of the date range.
 * @param end   Last day of the date range.
 */
data class AnalysisPeriod(
    val start: LocalDate,
    val end: LocalDate
) {

    /**
     * Initializes the instance.
     */
    init {
        require(!start.isAfter(end)) { "Start day cannot be after end day" }
        require(!end.isBefore(start)) { "End day cannot be before start day" }
    }


    /**
     * Returns the hash code of the instance.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash = start.hashCode()
        hash = 31 * hash + end.hashCode()
        return hash
    }


    /**
     * Tests whether the passed object is identical to this instance.
     *
     * @param other Other object to test.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is AnalysisPeriod
                && other.start == this.start
                && other.end == this.end
    }


    /**
     * Converts the instance into a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return "[Start: ${start.format(formatter)}] [End: ${end.format(formatter)}]"
    }


    companion object {

        /**
         * Default instance storing the time period of the last 12 months.
         */
        val CURRENT_YEAR = AnalysisPeriod(
            start = LocalDate.now().minusYears(1).plusDays(1),
            end = LocalDate.now()
        )

        /**
         * Default instance storing a time period of 12 months one year ago.
         */
        val LAST_YEAR = AnalysisPeriod(
            start = LocalDate.now().minusYears(2).plusDays(1),
            end = LocalDate.now().minusYears(1)
        )

    }

}
