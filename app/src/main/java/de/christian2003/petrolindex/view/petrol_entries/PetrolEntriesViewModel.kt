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


class PetrolEntriesViewModel: ViewModel() {

    private lateinit var repository: PetrolIndexRepository

    lateinit var petrolEntries: Flow<List<PetrolEntry>>

    var petrolEntryToDelete by mutableStateOf<PetrolEntry?>(null)


    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        petrolEntries = repository.allPetrolEntries
        petrolEntryToDelete = null
    }


    fun delete(petrolEntry: PetrolEntry) = viewModelScope.launch {
        repository.deletePetrolEntry(petrolEntry)
    }

}
