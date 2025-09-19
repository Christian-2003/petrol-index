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
class GetConsumptionUseCaseUnitTest {

    @Mock
    private lateinit var consumptionRepositoryMock: ConsumptionRepository

    private lateinit var useCase: GetConsumptionUseCase

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

        useCase = GetConsumptionUseCase(consumptionRepositoryMock)
    }


    @Test
    fun getExistingConsumption() = runTest {
        val consumption: Consumption? = useCase.getConsumption(validId)

        Assert.assertNotNull(consumption)
        Assert.assertEquals(validId, consumption!!.id)
    }


    @Test
    fun getNonExistingConsumption() = runTest {
        val consumption: Consumption? = useCase.getConsumption(invalidId)

        Assert.assertNull(consumption)
    }

}
