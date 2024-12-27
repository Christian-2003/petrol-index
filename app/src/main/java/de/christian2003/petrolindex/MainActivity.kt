package de.christian2003.petrolindex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
import org.jetbrains.annotations.Async.Execute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetrolIndex()
        }
    }
}


@Composable
fun PetrolIndex() {
    val navController = rememberNavController()
    val database = PetrolIndexDatabase.getInstance(LocalContext.current)
    val repository = PetrolIndexRepository(database.petrolEntryDao)

    val mainViewModel: MainViewModel = viewModel()
    mainViewModel.init(repository)

    val petrolEntriesViewModel: PetrolEntriesViewModel = viewModel()
    petrolEntriesViewModel.init(repository)

    val addPetrolEntryViewModel: AddPetrolEntryViewModel = viewModel()

    val settingsViewModel: SettingsViewModel = viewModel()
    settingsViewModel.init(repository)

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
