package de.christian2003.petrolindex.view.diagram

import android.app.Application
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import de.christian2003.petrolindex.model.diagram.DiagramInfo
import de.christian2003.petrolindex.model.diagram.DiagramType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Class implements the view model for the view displaying a diagram.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class DiagramViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Attribute stores the repository from which to get the data.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the diagram type to display.
     */
    private lateinit var type: DiagramType

    /**
     * Attribute stores the petrol entries for which to display the diagram.
     */
    lateinit var petrolEntries: Flow<List<PetrolEntry>>

    /**
     * Attribute stores the diagram info for the diagram to display.
     */
    var diagramInfo: DiagramInfo? by mutableStateOf(null)


    /**
     * Method initializes the view model.
     *
     * @param repository    Repository from which to get the data.
     * @param type          Type of the diagram to display.
     */
    fun init(repository: PetrolIndexRepository, type: DiagramType) {
        this.repository = repository
        this.type = type
        petrolEntries = repository.allPetrolEntries
    }


    /**
     * Method loads the diagram info.
     *
     * @param petrolEntries Petrol entries for which to create the diagram.
     * @param colorScheme   Color scheme for the diagram.
     */
    fun loadData(petrolEntries: List<PetrolEntry>, colorScheme: ColorScheme) = viewModelScope.launch {
        if (diagramInfo == null && petrolEntries.isNotEmpty()) {
            diagramInfo = DiagramInfo.createInstance(
                petrolEntries = petrolEntries,
                context = getApplication<Application>().baseContext,
                colorScheme = colorScheme,
                type = type)
        }
    }

}
