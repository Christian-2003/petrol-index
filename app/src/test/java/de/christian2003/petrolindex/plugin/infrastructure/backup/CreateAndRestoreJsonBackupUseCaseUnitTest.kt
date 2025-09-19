package de.christian2003.petrolindex.plugin.infrastructure.backup

import de.christian2003.petrolindex.application.backup.BackupRepository
import de.christian2003.petrolindex.application.backup.RestoreStrategy
import de.christian2003.petrolindex.domain.model.Consumption
import kotlinx.coroutines.flow.flowOf
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


@RunWith(MockitoJUnitRunner::class)
class CreateAndRestoreJsonBackupUseCaseUnitTest {

    @Mock
    private lateinit var appInfoProvider: AppInfoProvider

    @Mock
    private lateinit var backupRepository: BackupRepository

    private lateinit var useCase: CreateAndRestoreJsonBackupUseCase

    private val consumptionsDate: LocalDate = LocalDate.of(2025, 9, 19)

    private val consumptions: List<Consumption> = listOf(
        Consumption(
            volume = 1500,
            totalPrice = 3000,
            consumptionDate = consumptionsDate,
            description = "Consumption 1"
        ),
        Consumption(
            volume = 2000,
            totalPrice = 4000,
            consumptionDate = consumptionsDate,
            description = "Consumption 2"
        ),
        Consumption(
            volume = 2500,
            totalPrice = 5000,
            consumptionDate = consumptionsDate,
            description = "Consumption 3"
        )
    )


    @Before
    fun setup() {
        appInfoProvider = mock<AppInfoProvider>()
        whenever(appInfoProvider.getAppVersion()).thenReturn("1.2.3")
        whenever(appInfoProvider.getAppName()).thenReturn("Petrol Index")

        backupRepository = mock<BackupRepository>()
        whenever(backupRepository.getAllConsumptions()).thenReturn(flowOf(consumptions))

        useCase = CreateAndRestoreJsonBackupUseCase(backupRepository, appInfoProvider)
    }


    @Test
    fun createV2Backup() = runTest {
        var json: String? = useCase.create()

        Assert.assertNotNull(json)

        json = json!!.replaceFirst(Regex("\"created\":\".*?\","), "\"created\":\"yyyy-MM-dd\",")
        json = json.replace(" ", "") //Remove spaces

        val correctJson = """
            {
                "metadata": {
                    "created": "yyyy-MM-dd",
                    "appVersion": "1.2.3",
                    "appName": "Petrol Index",
                    "version": 2
                },
                "consumptions": [
                    {
                        "id": "${consumptions[0].id}",
                        "volume": 1500,
                        "totalPrice": 3000,
                        "date": "2025-09-19",
                        "description": "Consumption 1",
                        "distanceTraveled": null
                    },
                    {
                        "id": "${consumptions[1].id}",
                        "volume": 2000,
                        "totalPrice": 4000,
                        "date": "2025-09-19",
                        "description": "Consumption 2",
                        "distanceTraveled": null
                    },
                    {
                        "id": "${consumptions[2].id}",
                        "volume": 2500,
                        "totalPrice": 5000,
                        "date": "2025-09-19",
                        "description": "Consumption 3",
                        "distanceTraveled": null
                    }
                ]
            }
        """.replace(" ", "").replace("\n", "")

        Assert.assertEquals(correctJson, json)
    }


    @Test
    fun restoreV2Backup() = runTest {
        val json = """
            {
                "metadata": {
                    "created": "2025-09-19T16:49:55.892542",
                    "appVersion": "1.2.3",
                    "appName": "Petrol Index",
                    "version": 2
                },
                "consumptions": [
                    {
                        "id": "${consumptions[0].id}",
                        "volume": 1500,
                        "totalPrice": 3000,
                        "date": "2025-09-19",
                        "description": "Consumption 1",
                        "distanceTraveled": null
                    },
                    {
                        "id": "${consumptions[1].id}",
                        "volume": 2000,
                        "totalPrice": 4000,
                        "date": "2025-09-19",
                        "description": "Consumption 2",
                        "distanceTraveled": null
                    },
                    {
                        "id": "${consumptions[2].id}",
                        "volume": 2500,
                        "totalPrice": 5000,
                        "date": "2025-09-19",
                        "description": "Consumption 3",
                        "distanceTraveled": null
                    }
                ]
            }
        """.replace(" ", "").replace("\n", "")

        val success: Boolean = useCase.restore(json, RestoreStrategy.DELETE_EXISTING_DATA)

        Assert.assertTrue(success)
    }


    @Test
    fun restoreV1Backup() = runTest {
        val json = """
            [
                {
                    "id": 44,
                    "epochSecond": 1758123361,
                    "volume": 1500,
                    "totalPrice": 3000,
                    "description": "Consumption 1"
                },
                {
                    "id": 43,
                    "epochSecond": 1758123361,
                    "volume": 2000,
                    "totalPrice": 4000,
                    "description": "Consumption 2"
                },
                {
                    "id": 42,
                    "epochSecond": 1758123361,
                    "volume": 2500,
                    "totalPrice": 5000,
                    "description": "Consumption 13",
                    "distanceTraveled": 350
                }
            ]
        """.replace(" ", "").replace("\n", "")

        val success: Boolean = useCase.restore(json, RestoreStrategy.DELETE_EXISTING_DATA)

        Assert.assertTrue(success)
    }

}
