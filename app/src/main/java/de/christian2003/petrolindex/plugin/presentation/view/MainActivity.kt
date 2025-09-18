package de.christian2003.petrolindex.plugin.presentation.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.application.usecases.CreateConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.DeleteConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.GetAllConsumptionsUseCase
import de.christian2003.petrolindex.application.usecases.GetConsumptionUseCase
import de.christian2003.petrolindex.application.usecases.UpdateConsumptionUseCase
import de.christian2003.petrolindex.plugin.infrastructure.db.PetrolIndexDatabase
import de.christian2003.petrolindex.plugin.infrastructure.db.PetrolIndexRepository
import de.christian2003.petrolindex.model.update.UpdateManager
import de.christian2003.petrolindex.plugin.presentation.ui.theme.PetrolIndexTheme
import de.christian2003.petrolindex.plugin.presentation.view.consumption.ConsumptionScreen
import de.christian2003.petrolindex.plugin.presentation.view.consumption.ConsumptionViewModel
import de.christian2003.petrolindex.plugin.presentation.view.licenses.LicensesView
import de.christian2003.petrolindex.plugin.presentation.view.licenses.LicensesViewModel
import de.christian2003.petrolindex.plugin.presentation.view.main.MainView
import de.christian2003.petrolindex.plugin.presentation.view.main.MainViewModel
import de.christian2003.petrolindex.plugin.presentation.view.consumptions.ConsumptionsScreen
import de.christian2003.petrolindex.plugin.presentation.view.consumptions.ConsumptionsViewModel
import de.christian2003.petrolindex.plugin.presentation.view.help.HelpScreen
import de.christian2003.petrolindex.plugin.presentation.view.help.HelpViewModel
import de.christian2003.petrolindex.plugin.presentation.view.settings.SettingsView
import de.christian2003.petrolindex.plugin.presentation.view.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


/**
 * Class implements the main activity of the app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class MainActivity : ComponentActivity() {

    /**
     * Attribute stores the update manager through which to detect and download new app updates.
     */
    private var updateManager: UpdateManager? = null


    /**
     * Method is called when the main activity is (re)created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Update manager
        if (updateManager == null) {
            updateManager = UpdateManager()
            updateManager!!.init(this)
        }

        //Splash screen:
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(resources.getInteger(R.integer.splash_duration).toLong())
            keepSplashScreen = false
        }

        //App content:
        enableEdgeToEdge()
        setContent {
            PetrolIndex(
                writeToFile = { uri, content ->
                    writeToFile(uri, content)
                },
                readFromFile = {uri ->
                    readFromFile(uri)
                },
                updateManager = updateManager!!
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
 * @param updateManager Update manager through which to detect and download app updates.
 */
@Composable
fun PetrolIndex(
    writeToFile: (Uri, String) -> Boolean,
    readFromFile: (Uri) -> String?,
    updateManager: UpdateManager
) {
    val navController = rememberNavController()
    val database = PetrolIndexDatabase.getInstance(LocalContext.current)
    val repository = PetrolIndexRepository(database.petrolEntryDao)

    PetrolIndexTheme {
        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                val mainViewModel: MainViewModel = viewModel()
                mainViewModel.init(repository, updateManager)

                MainView(
                    viewModel = mainViewModel,
                    onNavigateToPetrolEntries = {
                        navController.navigate("consumptions")
                    },
                    onNavigateToAddPetrolEntry = {
                        navController.navigate("consumptions/")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    },
                    onNavigateToDiagram = { diagramInfo ->
                        navController.navigate("diagram/${diagramInfo.type.ordinal}")
                    }
                )
            }

            composable("consumptions") {
                val viewModel: ConsumptionsViewModel = viewModel()
                viewModel.init(
                    getAllConsumptionsUseCase = GetAllConsumptionsUseCase(repository),
                    deleteConsumptionUseCase = DeleteConsumptionUseCase(repository)
                )

                ConsumptionsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onEditConsumption = { id ->
                        val idAsString: String = id.toString()
                        navController.navigate("consumptions/$idAsString")
                    }
                )
            }

            composable(
                route = "consumptions/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id: Uuid? = try {
                    UUID.fromString(backStackEntry.arguments?.getString("id")!!).toKotlinUuid()
                } catch(e: Exception) {
                    null
                }
                val viewModel: ConsumptionViewModel = viewModel()
                viewModel.init(
                    updateConsumptionUseCase = UpdateConsumptionUseCase(repository),
                    createConsumptionUseCase = CreateConsumptionUseCase(repository),
                    getConsumptionUseCase = GetConsumptionUseCase(repository),
                    id = id
                )
                ConsumptionScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }

            composable("settings") {
                val viewModel: SettingsViewModel = viewModel()
                viewModel.init(repository, writeToFile, readFromFile)

                SettingsView(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToLicenses = {
                        navController.navigate("licenses")
                    },
                    onNavigateToHelp = {
                        navController.navigate("help")
                    }
                )
            }

            composable("help") {
                val viewModel: HelpViewModel = viewModel()
                viewModel.init()

                HelpScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }

            composable("licenses") {
                val viewModel: LicensesViewModel = viewModel()
                viewModel.init()

                LicensesView(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
