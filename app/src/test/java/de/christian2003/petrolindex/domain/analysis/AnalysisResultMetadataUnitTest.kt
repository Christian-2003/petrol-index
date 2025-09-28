package de.christian2003.petrolindex.domain.analysis

import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime


class AnalysisResultMetadataUnitTest {

    @Test
    fun createInstance() {
        AnalysisResultMetadata(
            start = LocalDate.now(),
            end = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            analyzedConsumptionCount = 10,
            analysisTimeMillis = 5000,
            precision = AnalysisPrecision.MONTH
        )
    }


    @Test
    fun createWithIdenticalStartAndEndDates() {
        val date = LocalDate.now()
        AnalysisResultMetadata(
            start = date,
            end = date,
            createdAt = LocalDateTime.now(),
            analyzedConsumptionCount = 10,
            analysisTimeMillis = 5000,
            precision = AnalysisPrecision.MONTH
        )
    }


    @Test
    fun createWithStartDateAfterEndDate() {
        val start = LocalDate.now()
        val end = start.minusDays(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultMetadata(
                start = start,
                end = end,
                createdAt = LocalDateTime.now(),
                analyzedConsumptionCount = 10,
                analysisTimeMillis = 5000,
                precision = AnalysisPrecision.MONTH
            )
        }
    }


    @Test
    fun createWithNoAnalyzedConsumptionCount() {
        AnalysisResultMetadata(
            start = LocalDate.now(),
            end = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            analyzedConsumptionCount = 0,
            analysisTimeMillis = 5000,
            precision = AnalysisPrecision.MONTH
        )
    }


    @Test
    fun createWithNoAnalysisTimeMillis() {
        AnalysisResultMetadata(
            start = LocalDate.now(),
            end = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            analyzedConsumptionCount = 10,
            analysisTimeMillis = 0,
            precision = AnalysisPrecision.MONTH
        )
    }


    @Test
    fun createWithNegativeAnalyzedConsumptionCount() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultMetadata(
                start = LocalDate.now(),
                end = LocalDate.now(),
                createdAt = LocalDateTime.now(),
                analyzedConsumptionCount = -1,
                analysisTimeMillis = 5000,
                precision = AnalysisPrecision.MONTH
            )
        }
    }


    @Test
    fun createWithNegativeAnalysisTimeMillis() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultMetadata(
                start = LocalDate.now(),
                end = LocalDate.now(),
                createdAt = LocalDateTime.now(),
                analyzedConsumptionCount = 10,
                analysisTimeMillis = -1,
                precision = AnalysisPrecision.MONTH
            )
        }
    }


    @Test
    fun testEqualsAndHashCode() {
        val date = LocalDate.now()
        val dateTime = LocalDateTime.now()

        val metadata1 = AnalysisResultMetadata(
            start = date,
            end = date,
            createdAt = dateTime,
            analyzedConsumptionCount = 10,
            analysisTimeMillis = 5000,
            precision = AnalysisPrecision.MONTH
        )
        val metadata2 = AnalysisResultMetadata(
            start = date,
            end = date,
            createdAt = dateTime,
            analyzedConsumptionCount = 10,
            analysisTimeMillis = 5000,
            precision = AnalysisPrecision.MONTH
        )
        val metadata3 = AnalysisResultMetadata(
            start = date,
            end = date,
            createdAt = dateTime,
            analyzedConsumptionCount = 100,
            analysisTimeMillis = 20000,
            precision = AnalysisPrecision.MONTH
        )

        Assert.assertEquals(true, metadata1 == metadata2)
        Assert.assertEquals(false, metadata1 == metadata3)
        Assert.assertEquals(metadata1.hashCode(), metadata2.hashCode())
        Assert.assertNotEquals(metadata1.hashCode(), metadata3.hashCode())
    }

}
