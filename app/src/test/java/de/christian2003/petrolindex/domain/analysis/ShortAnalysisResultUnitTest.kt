package de.christian2003.petrolindex.domain.analysis

import org.junit.Assert
import org.junit.Test


class ShortAnalysisResultUnitTest {

    @Test
    fun createShortAnalysisResult() {
        ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 1000,
            totalPrice = 1800,
            totalDistanceTraveled = 350
        )
    }


    @Test
    fun createWithoutAveragePricePerLiter() {
        ShortAnalysisResult(
            averagePricePerLiter = 0,
            totalVolume = 1000,
            totalPrice = 1800,
            totalDistanceTraveled = 350
        )
    }


    @Test
    fun createWithoutTotalVolume() {
        ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 0,
            totalPrice = 1800,
            totalDistanceTraveled = 350
        )
    }


    @Test
    fun createWithoutTotalPrice() {
        ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 1000,
            totalPrice = 0,
            totalDistanceTraveled = 350
        )
    }


    @Test
    fun createWithoutTotalDistanceTraveled() {
        ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 1000,
            totalPrice = 1800,
            totalDistanceTraveled = 0
        )
    }


    @Test
    fun createWithNegativeAveragePricePerLiter() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            ShortAnalysisResult(
                averagePricePerLiter = -1,
                totalVolume = 1000,
                totalPrice = 1800,
                totalDistanceTraveled = 350
            )
        }
    }


    @Test
    fun createWithNegativeTotalVolume() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            ShortAnalysisResult(
                averagePricePerLiter = 100,
                totalVolume = -1,
                totalPrice = 1800,
                totalDistanceTraveled = 350
            )
        }
    }


    @Test
    fun createWithNegativeTotalPrice() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            ShortAnalysisResult(
                averagePricePerLiter = 100,
                totalVolume = 1000,
                totalPrice = -1,
                totalDistanceTraveled = 350
            )
        }
    }


    @Test
    fun createWithNegativeTotalDistanceTraveled() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            ShortAnalysisResult(
                averagePricePerLiter = 100,
                totalVolume = 1000,
                totalPrice = 1800,
                totalDistanceTraveled = -1
            )
        }
    }


    @Test
    fun testEqualsAndHashCode() {
        val result1 = ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 1000,
            totalPrice = 1800,
            totalDistanceTraveled = 350
        )
        val result2 = ShortAnalysisResult(
            averagePricePerLiter = 100,
            totalVolume = 1000,
            totalPrice = 1800,
            totalDistanceTraveled = 350
        )
        val result3 = ShortAnalysisResult(
            averagePricePerLiter = 10,
            totalVolume = 100,
            totalPrice = 180,
            totalDistanceTraveled = 0
        )

        Assert.assertEquals(true, result1 == result2)
        Assert.assertEquals(false, result1 == result3)
        Assert.assertEquals(result1.hashCode(), result2.hashCode())
        Assert.assertNotEquals(result1.hashCode(), result3.hashCode())
    }

}
