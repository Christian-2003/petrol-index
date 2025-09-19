package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import java.time.LocalDate


@RunWith(MockitoJUnitRunner::class)
class CreateConsumptionUseCaseUnitTest {

    @Mock
    private lateinit var consumptionRepositoryMock: ConsumptionRepository

    private lateinit var useCase: CreateConsumptionUseCase


    @Before
    fun setup() {
        consumptionRepositoryMock = mock<ConsumptionRepository>()
        useCase = CreateConsumptionUseCase(consumptionRepositoryMock)
    }


    @Test
    fun createConsumption() = runTest {
        useCase.createConsumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun createConsumptionWithNegativeVolume() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.createConsumption(
                    volume = -1,
                    totalPrice = 3000,
                    consumptionDate = LocalDate.now(),
                    description = "",
                    distanceTraveled = null
                )
            }
        }
    }


    @Test
    fun createConsumptionWithNegativeTotalPrice() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.createConsumption(
                    volume = 1500,
                    totalPrice = -1,
                    consumptionDate = LocalDate.now(),
                    description = "",
                    distanceTraveled = null
                )
            }
        }
    }


    @Test
    fun createConsumptionWithNegativeDistanceTraveled() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.createConsumption(
                    volume = 1500,
                    totalPrice = 3000,
                    consumptionDate = LocalDate.now(),
                    description = "",
                    distanceTraveled = -1
                )
            }
        }
    }


    @Test
    fun createConsumptionWithNoVolume() = runTest {
        useCase.createConsumption(
            volume = 0,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun createConsumptionWithNoTotalPrice() = runTest {
        useCase.createConsumption(
            volume = 1500,
            totalPrice = 0,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun createConsumptionWithNoDistanceTraveled() = runTest {
        useCase.createConsumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = 0
        )
    }


    @Test
    fun createConsumptionWithBlankDescription() = runTest {
        useCase.createConsumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = " ",
            distanceTraveled = 0
        )
    }

}
