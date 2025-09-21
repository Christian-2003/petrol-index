package de.christian2003.petrolindex.domain.consumption

import de.christian2003.petrolindex.domain.model.Consumption
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import kotlin.uuid.Uuid


class ConsumptionUnitTest {

    @Test
    fun createConsumption() {
        val id: Uuid = Uuid.random()
        val date: LocalDate = LocalDate.now()

        val c = Consumption(
            id = id,
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = date,
            description = "Hello, World",
            distanceTraveled = 350
        )

        Assert.assertEquals(id, c.id)
        Assert.assertEquals(1500, c.volume)
        Assert.assertEquals(3000, c.totalPrice)
        Assert.assertEquals(date, c.consumptionDate)
        Assert.assertEquals("Hello, World", c.description)
        Assert.assertEquals(350, c.distanceTraveled)
    }


    @Test
    fun createConsumptionWithNegativeVolume() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Consumption(
                volume = -1,
                totalPrice = 3000,
                consumptionDate = LocalDate.now(),
            )
        }
    }


    @Test
    fun createConsumptionWithNegativeTotalPrice() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Consumption(
                volume = 1500,
                totalPrice = -1,
                consumptionDate = LocalDate.now(),
            )
        }
    }


    @Test
    fun createConsumptionWithNegativeDistanceTraveled() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Consumption(
                volume = 1500,
                totalPrice = 3000,
                consumptionDate = LocalDate.now(),
                distanceTraveled = -1
            )
        }
    }


    @Test
    fun createConsumptionWithNoVolume() {
        Consumption(
            volume = 0,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
        )
    }


    @Test
    fun createConsumptionWithNoTotalPrice() {
        Consumption(
            volume = 1500,
            totalPrice = 0,
            consumptionDate = LocalDate.now(),
        )
    }


    @Test
    fun createConsumptionWithNoDistanceTraveled() {
        Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            distanceTraveled = 0
        )
    }


    @Test
    fun createConsumptionWithBlankDescription() {
        val c = Consumption(
            volume = 1500,
            totalPrice = 0,
            consumptionDate = LocalDate.now(),
            description = " "
        )

        Assert.assertEquals("", c.description)
    }


    @Test
    fun createConsumptionWithNilId() {
        Assert.assertThrows(java.lang.IllegalArgumentException::class.java) {
            Consumption(
                id = Uuid.NIL,
                volume = 1500,
                totalPrice = 3000,
                consumptionDate = LocalDate.now()
            )
        }
    }


    @Test
    fun calculatePricePerLiter() {
        val c = Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now()
        )

        Assert.assertEquals(200, c.calculatePricePerLiter())
    }


    @Test
    fun calculatePricePerLiterWithNoVolume() {
        val c = Consumption(
            volume = 0,
            totalPrice = 3000,
            consumptionDate = LocalDate.now()
        )

        Assert.assertEquals(0, c.calculatePricePerLiter())
    }


    @Test
    fun testHashCode() {
        val id: Uuid = Uuid.random()
        val c = Consumption(
            id = id,
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now()
        )

        Assert.assertEquals(id.hashCode(), c.hashCode())
    }


    @Test
    fun testEquals() {
        val id: Uuid = Uuid.random()
        val c1 = Consumption(
            id = id,
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now()
        )
        val c2 = Consumption(
            id = id,
            volume = 3000,
            totalPrice = 1500,
            consumptionDate = LocalDate.now()
        )
        val c3 = Consumption(
            id = Uuid.random(),
            volume = 3000,
            totalPrice = 1500,
            consumptionDate = LocalDate.now()
        )

        Assert.assertEquals(true, c1 == c2)
        Assert.assertEquals(false, c1 == c3)
    }

}
