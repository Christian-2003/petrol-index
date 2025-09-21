package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class AnalysisDataInterpolator(
    private val precision: AnalysisPrecision
) {

    fun interpolate(dataPoints: List<AnalysisDataPoint>, fillWithZero: Boolean): List<AnalysisDataPoint> {
        if (dataPoints.isEmpty()) {
            return dataPoints
        }

        val stepMonths: Int = when(precision) {
            AnalysisPrecision.MONTH -> 1
            AnalysisPrecision.QUARTER -> 3
            AnalysisPrecision.YEAR -> 12
        }

        val result: MutableList<AnalysisDataPoint> = mutableListOf()

        for (i in 0 until dataPoints.size - 1) {
            val current: AnalysisDataPoint = dataPoints[i]
            val next: AnalysisDataPoint = dataPoints[i + 1]

            result.add(current)

            val interpolatedDataPoints: List<AnalysisDataPoint> = interpolateBetweenDataPoints(
                first = current,
                second = next,
                steps = stepMonths,
                fillWithZero = fillWithZero
            )

            result.addAll(interpolatedDataPoints)
        }

        result.add(dataPoints.last())
        return result
    }



    private fun interpolateBetweenDataPoints(first: AnalysisDataPoint, second: AnalysisDataPoint, steps: Int, fillWithZero: Boolean): List<AnalysisDataPoint> {
        val result: MutableList<AnalysisDataPoint> = mutableListOf()
        val unitsBetween: Int = ChronoUnit.MONTHS.between(first.date, second.date).toInt() / steps

        if (unitsBetween > 1) {
            val valueDiff: Int = second.value - first.value
            val stepValue: Double = valueDiff.toDouble() / unitsBetween

            for (i in 1 until unitsBetween) {
                val interpolatedDate: LocalDate = first.date.plusMonths((steps * i).toLong())
                val interpolatedValue: Int = if (fillWithZero) {
                    0
                } else {
                    (first.value.toDouble() + stepValue * i).toInt()
                }
                result.add(AnalysisDataPoint(interpolatedDate, interpolatedValue))
            }
        }

        return result
    }

}
