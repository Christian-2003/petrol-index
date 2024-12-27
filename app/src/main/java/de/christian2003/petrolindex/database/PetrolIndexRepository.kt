package de.christian2003.petrolindex.database


/**
 * Class implements a repository through which to access the petrol index
 * database.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class PetrolIndexRepository(

    /**
     * Attribute stores the DAO through which to access the data from the table
     * storing all petrol entries.
     */
    private val petrolEntryDao: PetrolEntryDao

) {

    /**
     * Attribute stores all petrol entries sorted by their date.
     */
    val allPetrolEntries = petrolEntryDao.selectAllPetrolEntriesSortedByDate()


    /**
     * Method inserts the petrol entry specified into the database.
     *
     * @param petrolEntry   Petrol entry to insert.
     */
    suspend fun insertPetrolEntry(petrolEntry: PetrolEntry) {
        petrolEntryDao.insert(petrolEntry)
    }

    /**
     * Method deletes the petrol entry specified from the database.
     *
     * @param petrolEntry   Petrol entry to delete.
     */
    suspend fun deletePetrolEntry(petrolEntry: PetrolEntry) {
        petrolEntryDao.delete(petrolEntry)
    }

    /**
     * Method returns the petrol entry of the passed ID.
     *
     * @param id    ID of the petrol entry to return.
     * @return      Petrol entry of the ID specified.
     */
    suspend fun getPetrolEntryById(id: Int): PetrolEntry? {
        return petrolEntryDao.getById(id)
    }

}
