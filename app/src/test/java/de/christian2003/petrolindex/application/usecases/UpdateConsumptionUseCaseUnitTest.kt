package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.uuid.Uuid


@RunWith(MockitoJUnitRunner::class)
class UpdateConsumptionUseCaseUnitTest {

    @Mock
    private lateinit var consumptionRepositoryMock: ConsumptionRepository

    private lateinit var useCase: UpdateConsumptionUseCase

    private val validId: Uuid = Uuid.random()

    private val invalidId: Uuid = Uuid.random()


    @Before
    fun setup() {
        val validConsumption = Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            id = validId
        )
        consumptionRepositoryMock = mock<ConsumptionRepository>()

        runTest {
            whenever(consumptionRepositoryMock.getConsumptionById(validId)).thenReturn(validConsumption)
            whenever(consumptionRepositoryMock.getConsumptionById(invalidId)).thenReturn(null)
        }

        useCase = UpdateConsumptionUseCase(consumptionRepositoryMock)
    }


    @Test
    fun updateExistingConsumption() = runTest {
        useCase.updateConsumption(
            id = validId,
            volume = 2000,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun updateNonExistingConsumption() = runTest {
        useCase.updateConsumption(
            id = invalidId,
            volume = 2000,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun updateExistingConsumptionWithNegativeVolume() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.updateConsumption(
                    id = validId,
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
    fun updateExistingConsumptionWithNegativeTotalPrice() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.updateConsumption(
                    id = validId,
                    volume = 2000,
                    totalPrice = -1,
                    consumptionDate = LocalDate.now(),
                    description = "",
                    distanceTraveled = null
                )
            }
        }
    }


    @Test
    fun updateExistingConsumptionWithNegativeDistanceTraveled() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            runTest {
                useCase.updateConsumption(
                    id = validId,
                    volume = 2000,
                    totalPrice = 3000,
                    consumptionDate = LocalDate.now(),
                    description = "",
                    distanceTraveled = -1
                )
            }
        }
    }


    @Test
    fun updateExistingConsumptionWithNoVolume() = runTest {
        useCase.updateConsumption(
            id = validId,
            volume = 0,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun updateExistingConsumptionWithNoTotalPrice() = runTest {
        useCase.updateConsumption(
            id = validId,
            volume = 2000,
            totalPrice = 0,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = null
        )
    }


    @Test
    fun updateExistingConsumptionWithNoDistanceTraveled() = runTest {
        useCase.updateConsumption(
            id = validId,
            volume = 2000,
            totalPrice = 3000,
            consumptionDate = LocalDate.now(),
            description = "",
            distanceTraveled = 0
        )
    }

}
