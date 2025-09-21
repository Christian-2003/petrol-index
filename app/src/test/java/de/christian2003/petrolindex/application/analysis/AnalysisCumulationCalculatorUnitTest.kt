package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisCumulationCalculatorUnitTest {

    @Test
    fun calculateCumulatedValues() {
        val values: List<AnalysisDataPoint> = listOf(
            AnalysisDataPoint(LocalDate.of(2025, 1, 1), 1),
            AnalysisDataPoint(LocalDate.of(2025, 2, 2), 2),
            AnalysisDataPoint(LocalDate.of(2025, 3, 3), 3),
            AnalysisDataPoint(LocalDate.of(2025, 4, 4), 7),
            AnalysisDataPoint(LocalDate.of(2025, 5, 5), 8),
            AnalysisDataPoint(LocalDate.of(2025, 6, 6), 9),
            AnalysisDataPoint(LocalDate.of(2025, 7, 7), 55),
            AnalysisDataPoint(LocalDate.of(2025, 8, 8), 56),
            AnalysisDataPoint(LocalDate.of(2025, 9, 9), 20),
            AnalysisDataPoint(LocalDate.of(2025, 10, 10), 25)
        )

        val calculator = AnalysisCumulationCalculator()

        val cumulated = calculator.calculateCumulateValuesFor(values)

        //Test correct size of result:
        Assert.assertEquals(values.size, cumulated.size)

        //Test cumulated values:
        Assert.assertEquals(1, cumulated[0].value)
        Assert.assertEquals(3, cumulated[1].value)
        Assert.assertEquals(6, cumulated[2].value)
        Assert.assertEquals(13, cumulated[3].value)
        Assert.assertEquals(21, cumulated[4].value)
        Assert.assertEquals(30, cumulated[5].value)
        Assert.assertEquals(85, cumulated[6].value)
        Assert.assertEquals(141, cumulated[7].value)
        Assert.assertEquals(161, cumulated[8].value)
        Assert.assertEquals(186, cumulated[9].value)

        //Test cumulated dates:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), cumulated[0].date)
        Assert.assertEquals(LocalDate.of(2025, 2, 2), cumulated[1].date)
        Assert.assertEquals(LocalDate.of(2025, 3, 3), cumulated[2].date)
        Assert.assertEquals(LocalDate.of(2025, 4, 4), cumulated[3].date)
        Assert.assertEquals(LocalDate.of(2025, 5, 5), cumulated[4].date)
        Assert.assertEquals(LocalDate.of(2025, 6, 6), cumulated[5].date)
        Assert.assertEquals(LocalDate.of(2025,7 ,7), cumulated[6].date)
        Assert.assertEquals(LocalDate.of(2025, 8, 8), cumulated[7].date)
        Assert.assertEquals(LocalDate.of(2025, 9, 9), cumulated[8].date)
        Assert.assertEquals(LocalDate.of(2025, 10, 10), cumulated[9].date)
    }

}
