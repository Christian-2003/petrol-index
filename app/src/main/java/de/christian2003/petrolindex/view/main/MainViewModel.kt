package de.christian2003.petrolindex.view.main

import androidx.lifecycle.ViewModel
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.flow.Flow


class MainViewModel: ViewModel() {

    private lateinit var repository: PetrolIndexRepository

    lateinit var petrolEntries: Flow<List<PetrolEntry>>


    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        petrolEntries = repository.allPetrolEntries
    }

}
