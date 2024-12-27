package de.christian2003.petrolindex.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Interface provides methods through which to access the data within the table
 * storing all petrol entries.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
@Dao
interface PetrolEntryDao {

    /**
     * Method selects all petrol entries sorted by their date.
     *
     * @return  List of entries sorted by date.
     */
    @Query("SELECT * FROM petrol_entries ORDER BY epochSecond DESC")
    fun selectAllPetrolEntriesSortedByDate() : Flow<List<PetrolEntry>>

    /**
     * Method inserts the petrol entry specified into the database.
     *
     * @param petrolEntry   Petrol entry to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(petrolEntry: PetrolEntry)

    /**
     * Method returns the petrol entry of the ID specified.
     *
     * @param id    ID of the entry to return.
     */
    @Query("SELECT * FROM petrol_entries WHERE id = :id")
    suspend fun getById(id: Int): PetrolEntry?

    /**
     * Method deletes the petrol entry specified from the database.
     *
     * @param petrolEntry   Petrol entry to delete.
     */
    @Delete
    suspend fun delete(petrolEntry: PetrolEntry)

}
