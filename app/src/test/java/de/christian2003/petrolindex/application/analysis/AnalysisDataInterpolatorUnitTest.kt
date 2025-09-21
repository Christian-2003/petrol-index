package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataPoint
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisDataInterpolatorUnitTest {

    @Test
    fun interpolateMonthly() {
        val values: List<AnalysisDataPoint> = listOf(
            AnalysisDataPoint(LocalDate.of(2025, 1, 1), 100),
            AnalysisDataPoint(LocalDate.of(2025, 2, 1), 200),
            AnalysisDataPoint(LocalDate.of(2025, 4, 1), 300),
            AnalysisDataPoint(LocalDate.of(2025, 5, 1), 400),
            AnalysisDataPoint(LocalDate.of(2025, 6, 1), 500),
            AnalysisDataPoint(LocalDate.of(2025, 9, 1), 800)
        )

        val interpolator = AnalysisDataInterpolator(AnalysisPrecision.MONTH)

        val interpolated = interpolator.interpolate(values, false)

        //Test size of result:
        Assert.assertEquals(9, interpolated.size)

        //Test interpolated values:
        Assert.assertEquals(100, interpolated[0].value)
        Assert.assertEquals(200, interpolated[1].value)
        Assert.assertEquals(250, interpolated[2].value) //Interpolated
        Assert.assertEquals(300, interpolated[3].value)
        Assert.assertEquals(400, interpolated[4].value)
        Assert.assertEquals(500, interpolated[5].value)
        Assert.assertEquals(600, interpolated[6].value) //Interpolated
        Assert.assertEquals(700, interpolated[7].value) //Interpolated
        Assert.assertEquals(800, interpolated[8].value)

        //Test interpolated dates:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), interpolated[0].date)
        Assert.assertEquals(LocalDate.of(2025, 2, 1), interpolated[1].date)
        Assert.assertEquals(LocalDate.of(2025, 3, 1), interpolated[2].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 4, 1), interpolated[3].date)
        Assert.assertEquals(LocalDate.of(2025, 5, 1), interpolated[4].date)
        Assert.assertEquals(LocalDate.of(2025, 6, 1), interpolated[5].date)
        Assert.assertEquals(LocalDate.of(2025, 7, 1), interpolated[6].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 8, 1), interpolated[7].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 9, 1), interpolated[8].date)
    }


    @Test
    fun interpolateQuarterly() {
        val values: List<AnalysisDataPoint> = listOf(
            AnalysisDataPoint(LocalDate.of(2024, 1, 1), 100),
            AnalysisDataPoint(LocalDate.of(2024, 4, 1), 200),
            AnalysisDataPoint(LocalDate.of(2024, 10, 1), 400),
            AnalysisDataPoint(LocalDate.of(2025, 7, 1), 700)
        )

        val interpolator = AnalysisDataInterpolator(AnalysisPrecision.QUARTER)

        val interpolated = interpolator.interpolate(values, false)

        //Test size of result:
        Assert.assertEquals(7, interpolated.size)

        //Test interpolated values:
        Assert.assertEquals(100, interpolated[0].value)
        Assert.assertEquals(200, interpolated[1].value)
        Assert.assertEquals(300, interpolated[2].value) //Interpolated
        Assert.assertEquals(400, interpolated[3].value)
        Assert.assertEquals(500, interpolated[4].value) //Interpolated
        Assert.assertEquals(600, interpolated[5].value) //Interpolated
        Assert.assertEquals(700, interpolated[6].value)

        //Test interpolated dates:
        Assert.assertEquals(LocalDate.of(2024, 1, 1), interpolated[0].date)
        Assert.assertEquals(LocalDate.of(2024, 4, 1), interpolated[1].date)
        Assert.assertEquals(LocalDate.of(2024, 7, 1), interpolated[2].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2024, 10, 1), interpolated[3].date)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), interpolated[4].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 4, 1), interpolated[5].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 7, 1), interpolated[6].date)
    }


    @Test
    fun interpolateYearly() {
        val values: List<AnalysisDataPoint> = listOf(
            AnalysisDataPoint(LocalDate.of(2019, 1, 1), 100),
            AnalysisDataPoint(LocalDate.of(2020, 1, 1), 200),
            AnalysisDataPoint(LocalDate.of(2022, 1, 1), 400),
            AnalysisDataPoint(LocalDate.of(2025, 1, 1), 700)
        )

        val interpolator = AnalysisDataInterpolator(AnalysisPrecision.YEAR)

        val interpolated = interpolator.interpolate(values, false)

        //Test size of result:
        Assert.assertEquals(7, interpolated.size)

        //Test interpolated values:
        Assert.assertEquals(100, interpolated[0].value)
        Assert.assertEquals(200, interpolated[1].value)
        Assert.assertEquals(300, interpolated[2].value) //Interpolated
        Assert.assertEquals(400, interpolated[3].value)
        Assert.assertEquals(500, interpolated[4].value) //Interpolated
        Assert.assertEquals(600, interpolated[5].value) //Interpolated
        Assert.assertEquals(700, interpolated[6].value)

        //Test interpolated dates:
        Assert.assertEquals(LocalDate.of(2019, 1, 1), interpolated[0].date)
        Assert.assertEquals(LocalDate.of(2020, 1, 1), interpolated[1].date)
        Assert.assertEquals(LocalDate.of(2021, 1, 1), interpolated[2].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2022, 1, 1), interpolated[3].date)
        Assert.assertEquals(LocalDate.of(2023, 1, 1), interpolated[4].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2024, 1, 1), interpolated[5].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 1, 1), interpolated[6].date)
    }


    @Test
    fun interpolateWithZeros() {
        val values: List<AnalysisDataPoint> = listOf(
            AnalysisDataPoint(LocalDate.of(2025, 1, 1), 100),
            AnalysisDataPoint(LocalDate.of(2025, 2, 1), 200),
            AnalysisDataPoint(LocalDate.of(2025, 4, 1), 300),
            AnalysisDataPoint(LocalDate.of(2025, 5, 1), 400),
            AnalysisDataPoint(LocalDate.of(2025, 6, 1), 500),
            AnalysisDataPoint(LocalDate.of(2025, 9, 1), 800)
        )

        val interpolator = AnalysisDataInterpolator(AnalysisPrecision.MONTH)

        val interpolated = interpolator.interpolate(values, true)

        //Test size of result:
        Assert.assertEquals(9, interpolated.size)

        //Test interpolated values:
        Assert.assertEquals(100, interpolated[0].value)
        Assert.assertEquals(200, interpolated[1].value)
        Assert.assertEquals(0, interpolated[2].value) //Interpolated
        Assert.assertEquals(300, interpolated[3].value)
        Assert.assertEquals(400, interpolated[4].value)
        Assert.assertEquals(500, interpolated[5].value)
        Assert.assertEquals(0, interpolated[6].value) //Interpolated
        Assert.assertEquals(0, interpolated[7].value) //Interpolated
        Assert.assertEquals(800, interpolated[8].value)

        //Test interpolated dates:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), interpolated[0].date)
        Assert.assertEquals(LocalDate.of(2025, 2, 1), interpolated[1].date)
        Assert.assertEquals(LocalDate.of(2025, 3, 1), interpolated[2].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 4, 1), interpolated[3].date)
        Assert.assertEquals(LocalDate.of(2025, 5, 1), interpolated[4].date)
        Assert.assertEquals(LocalDate.of(2025, 6, 1), interpolated[5].date)
        Assert.assertEquals(LocalDate.of(2025, 7, 1), interpolated[6].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 8, 1), interpolated[7].date) //Interpolated
        Assert.assertEquals(LocalDate.of(2025, 9, 1), interpolated[8].date)
    }

}
