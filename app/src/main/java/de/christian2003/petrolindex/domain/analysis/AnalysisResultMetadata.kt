package de.christian2003.petrolindex.domain.analysis

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Metadata for the analysis result.
 *
 * @param start                     Start day of the time period for which the consumption is analyzed.
 * @param end                       End day of the time period for which the consumption is analyzed.
 * @param createdAt                 Date time at which the analysis is finished.
 * @param analyzedConsumptionCount  Number of consumptions that are analyzed.
 * @param analysisTimeMillis        Time (in milliseconds) that the analysis ran.
 */
data class AnalysisResultMetadata(
    val start: LocalDate,
    val end: LocalDate,
    val createdAt: LocalDateTime,
    val analyzedConsumptionCount: Int,
    val analysisTimeMillis: Long,
    val precision: AnalysisPrecision
) {

    /**
     * Initializes the metadata instance.
     */
    init {
        require(!start.isAfter(end)) { "Start day must be before end day" }
        require(!end.isBefore(start)) { "End day must be after start day" }
        require(analyzedConsumptionCount >= 0) { "Analyzed consumption count cannot be less than 0" }
        require(analysisTimeMillis >= 0) { "Analyzed time millis cannot be less than 0" }
    }


    /**
     * Calculates the hash code of the metadata instance.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = start.hashCode()
        hash = 31 * hash + end.hashCode()
        hash = 31 * hash + createdAt.hashCode()
        hash = 31 * hash + analyzedConsumptionCount.hashCode()
        hash = 31 * hash + analysisTimeMillis.hashCode()
        hash = 31 * hash + precision.hashCode()
        return hash
    }


    /**
     * Tests whether the passed object is identical to this metadata instance.
     *
     * @param other Other object to test.
     * @return      Whether both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is AnalysisResultMetadata
                && other.start == this.start
                && other.end == this.end
                && other.createdAt == this.createdAt
                && other.analyzedConsumptionCount == this.analyzedConsumptionCount
                && other.analysisTimeMillis == this.analysisTimeMillis
                && other.precision == this.precision
    }


    /**
     * Converts the metadata instance to a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
        return "[Start: ${start.format(dateFormatter)}] [End: ${end.format(dateFormatter)}] [CreatedAt: ${createdAt.format(dateTimeFormatter)}] [AnalyzedConsumptionCount: $analyzedConsumptionCount] [AnalysisTimeMillis: $analysisTimeMillis] [Precision: $precision]"
    }

}
