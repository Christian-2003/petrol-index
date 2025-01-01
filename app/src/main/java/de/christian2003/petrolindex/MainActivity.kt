package de.christian2003.petrolindex

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.petrolindex.database.PetrolIndexDatabase
import de.christian2003.petrolindex.database.PetrolIndexRepository
import de.christian2003.petrolindex.ui.theme.PetrolIndexTheme
import de.christian2003.petrolindex.view.add_petrol_entry.AddPetrolEntryView
import de.christian2003.petrolindex.view.add_petrol_entry.AddPetrolEntryViewModel
import de.christian2003.petrolindex.view.main.MainView
import de.christian2003.petrolindex.view.main.MainViewModel
import de.christian2003.petrolindex.view.petrol_entries.PetrolEntriesView
import de.christian2003.petrolindex.view.petrol_entries.PetrolEntriesViewModel
import de.christian2003.petrolindex.view.settings.SettingsView
import de.christian2003.petrolindex.view.settings.SettingsViewModel


/**
 * Class implements the main activity of the app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class MainActivity : ComponentActivity() {

    /**
     * Method is called when the main activity is (re)created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetrolIndex(
                writeToFile = { uri, content ->
                    writeToFile(uri, content)
                },
                readFromFile = {uri ->
                    readFromFile(uri)
                }
            )
        }
    }


    /**
     * Method writes the content string specified to the file whose URI is passed as argument.
     * Afterwards, a boolean indicating whether the file operation was successful is returned.
     * This method blocks the calling dispatcher.
     *
     * @param uri       URI of the file to which to write the content.
     * @param content   Content to write to the file specified.
     * @return          Whether the file operation was successful.
     */
    private fun writeToFile(uri: Uri, content: String): Boolean {
        try {
            contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray())
                stream.flush()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            return false
        }
        return true
    }


    /**
     * Method reads the file whose URI is passed as argument. The content of the file is returned
     * as string afterwards. If the file cannot be read, null is returned. This method blocks the
     * calling dispatcher.
     *
     * @param uri   URI of the file to read.
     * @return      Content of the file as string.
     */
    private fun readFromFile(uri: Uri): String? {
        var content: String? = null
        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(1024)
                val stringBuilder = StringBuilder()
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    stringBuilder.append(String(buffer, 0, bytesRead))
                }
                content = stringBuilder.toString()
            }
        }
        catch (e: Exception) {
            Log.e("ReadFromFile", e.stackTraceToString())
            content = null
        }
        return content
    }

}


/**
 * Composable displays the entire application.
 *
 * @param writeToFile   Callback to invoke to write data to a file.
 * @param readFromFile  Callback to invoke to read data from a file.
 */
@Composable
fun PetrolIndex(
    writeToFile: (Uri, String) -> Boolean,
    readFromFile: (Uri) -> String?
) {
    val navController = rememberNavController()
    val database = PetrolIndexDatabase.getInstance(LocalContext.current)
    val repository = PetrolIndexRepository(database.petrolEntryDao)

    val mainViewModel: MainViewModel = viewModel()
    mainViewModel.init(repository)

    val petrolEntriesViewModel: PetrolEntriesViewModel = viewModel()
    petrolEntriesViewModel.init(repository)

    val addPetrolEntryViewModel: AddPetrolEntryViewModel = viewModel()

    val settingsViewModel: SettingsViewModel = viewModel()
    settingsViewModel.init(repository, writeToFile, readFromFile)

    PetrolIndexTheme {
        NavHost(
            navController = navController,
            startDestination = "main_view"
        ) {
            composable("main_view") {
                MainView(
                    viewModel = mainViewModel,
                    onNavigateToPetrolEntries = {
                        navController.navigate("petrol_entries_view")
                    },
                    onNavigateToAddPetrolEntry = {
                        navController.navigate("add_petrol_entry_view")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("petrol_entries_view") {
                PetrolEntriesView (
                    viewModel = petrolEntriesViewModel,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onPetrolEntrySelected = { id ->
                        navController.navigate("add_petrol_entry_view/$id")
                    }
                )
            }
            composable("add_petrol_entry_view") {
                addPetrolEntryViewModel.init(repository)
                AddPetrolEntryView (
                    viewModel = addPetrolEntryViewModel,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable("add_petrol_entry_view/{id}") { backStackEntry ->
                val id: Int? = try {
                    //Do not use Bundle.getInt() since it can for some reason not cast to an integer... \(°.°)/
                    backStackEntry.arguments?.getString("id")!!.toInt()
                } catch(e: Exception) {
                    null
                }
                if (id != null) {
                    addPetrolEntryViewModel.init(repository)
                    AddPetrolEntryView (
                        viewModel = addPetrolEntryViewModel,
                        onNavigateBack = {
                            navController.navigateUp()
                        },
                        id = id
                    )
                }
            }
            composable("settings") {
                SettingsView(
                    viewModel = settingsViewModel,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
