package de.christian2003.petrolindex.application.usecases

import de.christian2003.petrolindex.application.repository.ConsumptionRepository
import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.presentation.view.consumption.ConsumptionScreen
import kotlinx.coroutines.test.runTest
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
class DeleteConsumptionUseCaseUnitTest {

    @Mock
    private lateinit var consumptionRepositoryMock: ConsumptionRepository

    private lateinit var useCase: DeleteConsumptionUseCase

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

        useCase = DeleteConsumptionUseCase(consumptionRepositoryMock)
    }


    @Test
    fun deleteExistingConsumption() = runTest {
        useCase.deleteConsumption(validId)
    }


    @Test
    fun deleteNonExistingConsumption() = runTest {
        useCase.deleteConsumption(invalidId)
    }

}
