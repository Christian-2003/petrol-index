package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate


@RunWith(MockitoJUnitRunner::class)
class GetAllConsumptionsUseCaseUnitTest {

    private lateinit var consumptionRepositoryMock: ConsumptionRepository

    private lateinit var useCase: GetAllConsumptionsUseCase


    @Before
    fun setup() {
        val consumptions: List<Consumption> = listOf(
            Consumption(
                volume = 1500,
                totalPrice = 3000,
                consumptionDate = LocalDate.now(),
                description = "Consumption 1"
            ),
            Consumption(
                volume = 2000,
                totalPrice = 4000,
                consumptionDate = LocalDate.now(),
                description = "Consumption 2"
            ),
            Consumption(
                volume = 2500,
                totalPrice = 5000,
                consumptionDate = LocalDate.now(),
                description = "Consumption 3"
            )
        )

        consumptionRepositoryMock = mock<ConsumptionRepository>()
        whenever(consumptionRepositoryMock.getAllConsumptions()).thenReturn(flowOf(consumptions))

        useCase = GetAllConsumptionsUseCase(consumptionRepositoryMock)
    }


    @Test
    fun getConsumptions() = runTest {
        val result: Flow<List<Consumption>> = useCase.getAllConsumptions()
        val consumptions: List<Consumption> = result.first()

        Assert.assertEquals(3, consumptions.size)
        Assert.assertEquals("Consumption 1", consumptions[0].description)
        Assert.assertEquals("Consumption 2", consumptions[1].description)
        Assert.assertEquals("Consumption 3", consumptions[2].description)
    }

}
