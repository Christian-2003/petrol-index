package de.christian2003.petrolindex.view.settings

import androidx.lifecycle.ViewModel
import de.christian2003.petrolindex.database.PetrolIndexRepository


/**
 * Class implements the view model for the settings page.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class SettingsViewModel: ViewModel() {

    private lateinit var repository: PetrolIndexRepository


    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
    }

}
