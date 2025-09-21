package de.christian2003.petrolindex.application.analysis

import de.christian2003.petrolindex.application.analysis.dto.AnalysisDataSummary
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.model.Consumption
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisDataSummarizerUnitTest {

    private val consumptions1: List<Consumption> = listOf(
        //Sep
        Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.of(2025, 9, 19)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2025, 9, 21)
        ),
        Consumption(
            volume = 1500,
            totalPrice = 4000,
            distanceTraveled = 250,
            consumptionDate = LocalDate.of(2025, 9, 25)
        ),
        //Feb
        Consumption(
            volume = 1000,
            totalPrice = 2500,
            distanceTraveled = 200,
            consumptionDate = LocalDate.of(2025, 2, 24)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 2500,
            distanceTraveled = 350,
            consumptionDate = LocalDate.of(2025, 2, 25)
        ),
        //Aug
        Consumption(
            volume = 3000,
            totalPrice = 6500,
            consumptionDate = LocalDate.of(2025, 8, 14)
        ),
        Consumption(
            volume = 3000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2025, 8, 25)
        )
    )

    private val consumptions2: List<Consumption> = listOf(
        //Q2
        Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.of(2025, 4, 19)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2025, 4, 21)
        ),
        Consumption(
            volume = 1500,
            totalPrice = 4000,
            distanceTraveled = 250,
            consumptionDate = LocalDate.of(2025, 6, 25)
        ),
        //Q1
        Consumption(
            volume = 1000,
            totalPrice = 2500,
            distanceTraveled = 200,
            consumptionDate = LocalDate.of(2025, 1, 24)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 2500,
            distanceTraveled = 350,
            consumptionDate = LocalDate.of(2025, 2, 25)
        ),
        //Q4
        Consumption(
            volume = 3000,
            totalPrice = 6500,
            consumptionDate = LocalDate.of(2025, 12, 14)
        ),
        Consumption(
            volume = 3000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2025, 11, 25)
        )
    )

    private val consumptions3: List<Consumption> = listOf(
        //2024
        Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.of(2024, 4, 19)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2024, 4, 21)
        ),
        Consumption(
            volume = 1500,
            totalPrice = 4000,
            distanceTraveled = 250,
            consumptionDate = LocalDate.of(2024, 6, 25)
        ),
        //2023
        Consumption(
            volume = 1000,
            totalPrice = 2500,
            distanceTraveled = 200,
            consumptionDate = LocalDate.of(2023, 1, 24)
        ),
        Consumption(
            volume = 2000,
            totalPrice = 2500,
            distanceTraveled = 350,
            consumptionDate = LocalDate.of(2023, 2, 25)
        ),
        //2025
        Consumption(
            volume = 3000,
            totalPrice = 6500,
            consumptionDate = LocalDate.of(2025, 12, 14)
        ),
        Consumption(
            volume = 3000,
            totalPrice = 4000,
            consumptionDate = LocalDate.of(2025, 11, 25)
        )
    )


    @Test
    fun monthlySummaryWithData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.MONTH)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(consumptions1)

        //Test grouping:
        Assert.assertEquals(3, summary.size)

        //Test sorting:
        Assert.assertEquals(2, summary[0].date.monthValue)
        Assert.assertEquals(8, summary[1].date.monthValue)
        Assert.assertEquals(9, summary[2].date.monthValue)

        //Test summary Feb:
        Assert.assertEquals(LocalDate.of(2025, 2, 1), summary[0].date)
        Assert.assertEquals(3000, summary[0].volumeSum)
        Assert.assertEquals(5000, summary[0].totalPriceSum)
        Assert.assertEquals(550, summary[0].distanceTraveledSum)
        Assert.assertEquals(2, summary[0].volumeCount)
        Assert.assertEquals(2, summary[0].totalPriceCount)
        Assert.assertEquals(2, summary[0].distanceTraveledCount)

        //Test summary Aug:
        Assert.assertEquals(LocalDate.of(2025, 8, 1), summary[1].date)
        Assert.assertEquals(6000, summary[1].volumeSum)
        Assert.assertEquals(10500, summary[1].totalPriceSum)
        Assert.assertEquals(0, summary[1].distanceTraveledSum)
        Assert.assertEquals(2, summary[1].volumeCount)
        Assert.assertEquals(2, summary[1].totalPriceCount)
        Assert.assertEquals(0, summary[1].distanceTraveledCount)

        //Test summary Sep:
        Assert.assertEquals(LocalDate.of(2025, 9, 1), summary[2].date)
        Assert.assertEquals(5000, summary[2].volumeSum)
        Assert.assertEquals(11000, summary[2].totalPriceSum)
        Assert.assertEquals(250, summary[2].distanceTraveledSum)
        Assert.assertEquals(3, summary[2].volumeCount)
        Assert.assertEquals(3, summary[2].totalPriceCount)
        Assert.assertEquals(1, summary[2].distanceTraveledCount)
    }


    @Test
    fun quarterlySummaryWithData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.QUARTER)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(consumptions2)

        //Test grouping:
        Assert.assertEquals(3, summary.size)

        //Test sorting:
        Assert.assertEquals(1, summary[0].date.monthValue)
        Assert.assertEquals(4, summary[1].date.monthValue)
        Assert.assertEquals(10, summary[2].date.monthValue)

        //Test summary Q1:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), summary[0].date)
        Assert.assertEquals(3000, summary[0].volumeSum)
        Assert.assertEquals(5000, summary[0].totalPriceSum)
        Assert.assertEquals(550, summary[0].distanceTraveledSum)
        Assert.assertEquals(2, summary[0].volumeCount)
        Assert.assertEquals(2, summary[0].totalPriceCount)
        Assert.assertEquals(2, summary[0].distanceTraveledCount)

        //Test summary Q2:
        Assert.assertEquals(LocalDate.of(2025, 4, 1), summary[1].date)
        Assert.assertEquals(5000, summary[1].volumeSum)
        Assert.assertEquals(11000, summary[1].totalPriceSum)
        Assert.assertEquals(250, summary[1].distanceTraveledSum)
        Assert.assertEquals(3, summary[1].volumeCount)
        Assert.assertEquals(3, summary[1].totalPriceCount)
        Assert.assertEquals(1, summary[1].distanceTraveledCount)

        //Test summary Q4:
        Assert.assertEquals(LocalDate.of(2025, 10, 1), summary[2].date)
        Assert.assertEquals(6000, summary[2].volumeSum)
        Assert.assertEquals(10500, summary[2].totalPriceSum)
        Assert.assertEquals(0, summary[2].distanceTraveledSum)
        Assert.assertEquals(2, summary[2].volumeCount)
        Assert.assertEquals(2, summary[2].totalPriceCount)
        Assert.assertEquals(0, summary[2].distanceTraveledCount)
    }


    @Test
    fun yearlySummaryWithData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.YEAR)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(consumptions3)

        //Test grouping:
        Assert.assertEquals(3, summary.size)

        //Test sorting:
        Assert.assertEquals(2023, summary[0].date.year)
        Assert.assertEquals(2024, summary[1].date.year)
        Assert.assertEquals(2025, summary[2].date.year)

        //Test summary 2023:
        Assert.assertEquals(LocalDate.of(2023, 1, 1), summary[0].date)
        Assert.assertEquals(3000, summary[0].volumeSum)
        Assert.assertEquals(5000, summary[0].totalPriceSum)
        Assert.assertEquals(550, summary[0].distanceTraveledSum)
        Assert.assertEquals(2, summary[0].volumeCount)
        Assert.assertEquals(2, summary[0].totalPriceCount)
        Assert.assertEquals(2, summary[0].distanceTraveledCount)

        //Test summary 2024:
        Assert.assertEquals(LocalDate.of(2024, 1, 1), summary[1].date)
        Assert.assertEquals(5000, summary[1].volumeSum)
        Assert.assertEquals(11000, summary[1].totalPriceSum)
        Assert.assertEquals(250, summary[1].distanceTraveledSum)
        Assert.assertEquals(3, summary[1].volumeCount)
        Assert.assertEquals(3, summary[1].totalPriceCount)
        Assert.assertEquals(1, summary[1].distanceTraveledCount)

        //Test summary 2025:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), summary[2].date)
        Assert.assertEquals(6000, summary[2].volumeSum)
        Assert.assertEquals(10500, summary[2].totalPriceSum)
        Assert.assertEquals(0, summary[2].distanceTraveledSum)
        Assert.assertEquals(2, summary[2].volumeCount)
        Assert.assertEquals(2, summary[2].totalPriceCount)
        Assert.assertEquals(0, summary[2].distanceTraveledCount)
    }


    @Test
    fun monthlySummaryWithoutData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.MONTH)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(emptyList())

        Assert.assertEquals(0, summary.size)
    }


    @Test
    fun quarterlySummaryWithoutData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.QUARTER)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(emptyList())

        Assert.assertEquals(0, summary.size)
    }


    @Test
    fun yearlySummaryWithoutData() {
        val summarizer = AnalysisDataSummarizer(AnalysisPrecision.YEAR)
        val summary: List<AnalysisDataSummary> = summarizer.summarize(emptyList())

        Assert.assertEquals(0, summary.size)
    }

}
