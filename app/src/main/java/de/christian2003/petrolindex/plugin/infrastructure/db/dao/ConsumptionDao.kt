package de.christian2003.petrolindex.plugin.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Interface provides methods through which to access the consumptions within the Room database.
 */
@Dao
interface ConsumptionDao {

    /**
     * Returns all consumptions sorted by date.
     *
     * @return  List of consumptions sorted by date.
     */
    @Query("SELECT * FROM petrol_entries ORDER BY epochSecond DESC")
    fun selectAllConsumptions() : Flow<List<ConsumptionEntity>>


    /**
     * Returns the consumption whose ID is passed as argument. If no consumption with the specified
     * ID exists, null is returned.
     *
     * @param id    ID of the consumption to return.
     * @return      Consumption with the specified ID or null.
     */
    @Query("SELECT * FROM petrol_entries WHERE id = :id")
    suspend fun selectById(id: Uuid): ConsumptionEntity?


    /**
     * Inserts the specified consumption into the database.
     *
     * @param consumption   Consumption to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumption: ConsumptionEntity)

    /**
     * Updates the consumption that is passed as argument.
     *
     * @param consumption   Consumption to update.
     */
    @Update
    suspend fun update(consumption: ConsumptionEntity)


    /**
     * Deletes the specified consumption from the database.
     *
     * @param consumption   Consumption to delete.
     */
    @Delete
    suspend fun delete(consumption: ConsumptionEntity)


    /**
     * Method deletes all consumptions from the database.
     */
    @Query("DELETE FROM petrol_entries")
    suspend fun deleteAll()

}
