package de.christian2003.petrolindex.view.add_petrol_entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christian2003.petrolindex.database.PetrolEntry
import de.christian2003.petrolindex.database.PetrolIndexRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


class AddPetrolEntryViewModel: ViewModel() {

    private lateinit var repository: PetrolIndexRepository

    var editedPetrolEntry: PetrolEntry? = null

    var epochSecond: Long by mutableLongStateOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    var volume: String by mutableStateOf("")

    var totalPrice: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var showModalDatePickerDialog: Boolean by mutableStateOf(false)

    var insertSuccessful: Boolean by mutableStateOf(false)

    var totalPriceValid: Boolean by mutableStateOf(true)

    var volumeValid: Boolean by mutableStateOf(true)



    fun init(repository: PetrolIndexRepository) {
        this.repository = repository
        editedPetrolEntry = null
        epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        volume = ""
        totalPrice = ""
        description = ""
        totalPriceValid = true
        volumeValid = true
        showModalDatePickerDialog = false
        insertSuccessful = false
    }


    fun loadPetrolEntryToEdit(id: Int) = viewModelScope.launch {
        if (editedPetrolEntry == null) {
            val petrolEntry: PetrolEntry? = repository.getPetrolEntryById(id)
            if (petrolEntry != null) {
                editedPetrolEntry = petrolEntry
                epochSecond = petrolEntry.epochSecond
                volume = (petrolEntry.volume.toDouble() / 100).toString().format("%.2f")
                totalPrice = (petrolEntry.totalPrice.toDouble() / 100).toString().format("%.2f")
                description = petrolEntry.description
                totalPriceValid = true
                volumeValid = true
                showModalDatePickerDialog = false
                insertSuccessful = false
            }
        }
    }


    fun insert() = viewModelScope.launch {
        if (isTotalPriceValid() && isVolumeValid()) {
            if (totalPrice.isEmpty()) {
                totalPrice = "0"
            }
            if (volume.isEmpty()) {
                volume = "0"
            }
            val petrolEntry = PetrolEntry(
                id = if (editedPetrolEntry != null) { editedPetrolEntry!!.id } else { 0 },
                epochSecond = epochSecond,
                volume = (volume.toDouble() * 100.0).toInt(),
                totalPrice = (totalPrice.toDouble() * 100.0).toInt(),
                description = description
            )
            repository.insertPetrolEntry(petrolEntry)
            insertSuccessful = true
        }
        else {
            insertSuccessful = false
        }
    }


    /**
     * Method determines whether the total price entered is valid (i.e. if the string entered can
     * be parsed to a double).
     *
     * @return  Whether the total price entered is valid.
     */
    fun isTotalPriceValid(): Boolean {
        if (totalPrice.isNotEmpty()) {
            try {
                totalPrice.toDouble()
            }
            catch (e: Exception) {
                totalPriceValid = false
                return false
            }
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
        if (volume.isNotEmpty()) {
            try {
                volume.toDouble()
            }
            catch (e: Exception) {
                volumeValid = false
                return false
            }
        }
        volumeValid = true
        return true
    }

}
