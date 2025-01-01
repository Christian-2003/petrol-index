package de.christian2003.petrolindex.view.main

import androidx.lifecycle.ViewModel
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.flow.Flow


/**
 * Class implements the view model for the main view.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class MainViewModel: ViewModel() {

    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the list of petrol entries.
     */
    lateinit var petrolEntries: Flow<List<PetrolEntry>>


    /**
     * Method instantiates the view model.
     *
     * @param repository    Repository through which to access data.
     */
    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        petrolEntries = repository.allPetrolEntries
    }

}
