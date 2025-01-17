package de.christian2003.petrolindex.view.add_petrol_entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Class implements the view model for the view through which to add or edit a petrol entry.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class AddPetrolEntryViewModel: ViewModel() {

    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: PetrolIndexRepository

    /**
     * Attribute stores the petrol entry to edit. This is null if a new petrol entry is created with
     * the view associated.
     */
    var editedPetrolEntry: PetrolEntry? = null

    /**
     * Attribute stores the epoch second at which the petrol entry was created.
     */
    var epochSecond: Long by mutableLongStateOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    /**
     * Attribute stores the string representation of the volume consumed.
     */
    var volume: String by mutableStateOf("")

    /**
     * Attribute stores the string representation of the total price paid.
     */
    var totalPrice: String by mutableStateOf("")

    /**
     * Attribute stores the optional description of the petrol entry.
     */
    var description: String by mutableStateOf("")

    /**
     * Attribute stores the string representation of the distance traveled in km.
     */
    var distanceTraveled: String by mutableStateOf("")

    /**
     * Attribute indicates whether the date picker dialog is currently visible.
     */
    var showModalDatePickerDialog: Boolean by mutableStateOf(false)

    /**
     * Attribute indicates whether the string representation of the total price entered is valid.
     */
    var totalPriceValid: Boolean by mutableStateOf(true)

    /**
     * Attribute indicates whether the string representation of the volume entered is valid.
     */
    var volumeValid: Boolean by mutableStateOf(true)


    /**
     * Method instantiates the view model.
     *
     * @param repository    Repository through which to access the data.
     */
    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        editedPetrolEntry = null
        epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        volume = ""
        totalPrice = ""
        description = ""
        distanceTraveled = ""
        totalPriceValid = true
        volumeValid = true
        showModalDatePickerDialog = false
    }


    /**
     * Method loads the petrol entry whose ID is passed as argument.
     *
     * @param id    ID of the petrol entry to load.
     */
    fun loadPetrolEntryToEdit(id: Int) = viewModelScope.launch {
        if (editedPetrolEntry == null) {
            val petrolEntry: PetrolEntry? = repository.getPetrolEntryById(id)
            if (petrolEntry != null) {
                editedPetrolEntry = petrolEntry
                epochSecond = petrolEntry.epochSecond
                volume = (petrolEntry.volume.toDouble() / 100).toString().format("%.2f")
                totalPrice = (petrolEntry.totalPrice.toDouble() / 100).toString().format("%.2f")
                description = petrolEntry.description
                distanceTraveled = if (petrolEntry.distanceTraveled == null) { "" } else { petrolEntry.distanceTraveled.toString() }
                totalPriceValid = true
                volumeValid = true
                showModalDatePickerDialog = false
            }
        }
    }


    /**
     * Method inserts the petrol entry configured into the database. This either adds the entry to
     * the database or replaces an existing entry (If the entry is currently being edited).
     */
    fun insert() = viewModelScope.launch {
        if (isTotalPriceValid() && isVolumeValid()) {
            if (totalPrice.isEmpty()) {
                totalPrice = "0"
            }
            if (volume.isEmpty()) {
                volume = "0"
            }

            //Convert distance traveled:
            val distanceTraveledResult: Int? = try {
                distanceTraveled.toInt()
            } catch (e: Exception) {
                null
            }

            val petrolEntry = PetrolEntry(
                id = if (editedPetrolEntry != null) { editedPetrolEntry!!.id } else { 0 },
                epochSecond = epochSecond,
                volume = (volume.toDouble() * 100.0).toInt(),
                totalPrice = (totalPrice.toDouble() * 100.0).toInt(),
                description = description,
                distanceTraveled = distanceTraveledResult
            )
            repository.insertPetrolEntry(petrolEntry)
        }
    }


    /**
     * Method determines whether the total price entered is valid (i.e. if the string entered can
     * be parsed to a double).
     *
     * @return  Whether the total price entered is valid.
     */
    fun isTotalPriceValid(): Boolean {
        try {
            totalPrice.toDouble()
        }
        catch (e: Exception) {
            totalPriceValid = false
            return false
        }
        totalPriceValid = true
        return true
    }

    /**
     * Method determines whether the volume entered is valid (i.e. if the string entered can be
     * parsed to a double).
     *
     * @return  Whether the volume entered is valid.
     */
    fun isVolumeValid(): Boolean {
        try {
            volume.toDouble()
        }
        catch (e: Exception) {
            volumeValid = false
            return false
        }
        volumeValid = true
        return true
    }

}
