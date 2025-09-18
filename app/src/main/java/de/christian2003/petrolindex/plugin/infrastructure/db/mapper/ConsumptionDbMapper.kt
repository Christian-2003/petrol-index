package de.christian2003.petrolindex.plugin.infrastructure.db.mapper

import de.christian2003.petrolindex.domain.model.Consumption
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity


/**
 * Mapper maps the domain model 'Consumption' to the database entity.
 */
class ConsumptionDbMapper {

    /**
     * Maps the database entity that is passed as argument to the domain model 'Consumption'.
     *
     * @param entity    Database entity to map to the domain model 'Consumption'.
     * @return          Domain model 'Consumption'.
     */
    fun toDomain(entity: ConsumptionEntity): Consumption {
        return Consumption(
            volume = entity.volume,
            totalPrice = entity.totalPrice,
            consumptionDate = entity.epochSecond,
            description = entity.description,
            distanceTraveled = entity.distanceTraveled,
            id = entity.id
        )
    }


    /**
     * Maps the domain model 'Consumption' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Consumption' to map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Consumption): ConsumptionEntity {
        return ConsumptionEntity(
            volume = domain.volume,
            totalPrice = domain.totalPrice,
            epochSecond = domain.consumptionDate,
            description = domain.description,
            distanceTraveled = domain.distanceTraveled,
            id = domain.id
        )
    }

}
