package de.christian2003.petrolindex.plugin.infrastructure.backup.mapper

import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v1.PetrolEntryDto
import de.christian2003.petrolindex.plugin.infrastructure.backup.dto.v2.BackupConsumptionDto
import java.time.LocalDate
import kotlin.uuid.Uuid

class ConsumptionBackupMapper {

    fun toDto(domain: Consumption): BackupConsumptionDto {
        return BackupConsumptionDto(
            id = domain.id,
            volume = domain.volume,
            consumptionDate = domain.consumptionDate,
            totalPrice = domain.totalPrice,
            description = domain.description,
            distanceTraveled = domain.distanceTraveled
        )
    }


    fun toDomain(dto: BackupConsumptionDto): Consumption {
        return Consumption(
            id = dto.id,
            volume = dto.volume,
            consumptionDate = dto.consumptionDate,
            totalPrice = dto.totalPrice,
            description = dto.description,
            distanceTraveled = dto.distanceTraveled
        )
    }


    fun toDomain(dto: PetrolEntryDto): Consumption {
        return Consumption(
            id = Uuid.random(),
            volume = dto.volume,
            consumptionDate = LocalDate.ofEpochDay(dto.epochSecond / 86400),
            totalPrice = dto.totalPrice,
            description = dto.description,
            distanceTraveled = dto.distanceTraveled
        )
    }

}
