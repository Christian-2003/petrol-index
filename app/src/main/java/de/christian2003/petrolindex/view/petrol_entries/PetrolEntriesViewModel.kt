package de.christian2003.petrolindex.view.petrol_entries

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the view displaying all petrol entries.
 */
class PetrolEntriesViewModel: ViewModel() {

    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the list of petrol entries.
     */
    lateinit var petrolEntries: Flow<List<PetrolEntry>>

    /**
     * Attribute stores the petrol entry to delete while the dialog to confirm deletion is visible.
     */
    var petrolEntryToDelete by mutableStateOf<PetrolEntry?>(null)


    /**
     * Method instantiates the view model.
     *
     * @param repository    Repository through which to access the data.
     */
    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        petrolEntries = repository.allPetrolEntries
        petrolEntryToDelete = null
    }


    /**
     * Method deletes the petrol entry specified from the app.
     *
     * @param petrolEntry   Petrol entry to delete.
     */
    fun delete(petrolEntry: PetrolEntry) = viewModelScope.launch {
        repository.deletePetrolEntry(petrolEntry)
    }

}
