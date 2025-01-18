package de.christian2003.petrolindex.model.update

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.supersuman.apkupdater.ApkUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Class implements an update manager which detects whether new updates are available.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class UpdateManager {

    /**
     * Attribute stores the URL to the newest release.
     */
    private val url = "https://github.com/Christian-2003/petrol-index/releases/latest"

    /**
     * Attribute stores whether the update manager is currently scanning for updates.
     */
    private var isScanningForUpdates = false

    /**
     * Attribute stores whether the update manager has finished scanning for updates.
     */
    private var isFinishedScanningForUpdates = false

    /**
     * Attribute stores whether a download for the new app release has been started.
     */
    private var isDownloading = false

    /**
     * Attribute stores the APK updater instance.
     */
    private lateinit var updater: ApkUpdater

    /**
     * Attribute stores whether a new update is available.
     */
    var isUpdateAvailable: Boolean by mutableStateOf(false)


    /**
     * Method initializes the update manager.
     *
     * @param activity  Activity required to check for updates.
     */
    fun init(activity: Activity) {
        if (!isScanningForUpdates && !isFinishedScanningForUpdates) {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                try {
                    isScanningForUpdates = true
                    updater = ApkUpdater(activity, url)
                    updater.threeNumbers = true
                    if (updater.isNewUpdateAvailable() == true) {
                        isUpdateAvailable = true
                        Log.d("Updates", "New update available")
                    }
                    else {
                        Log.d("Updates", "No update available")
                    }
                }
                catch (e: Exception) {
                    Log.e("Updates", "Cannot scan for updates: ${e.message}")
                }
                finally {
                    isFinishedScanningForUpdates = true
                    isScanningForUpdates = false
                }
            }
        }
    }


    /**
     * Method begins downloading a new version of the app if available. If another download has been
     * started before (by calling this method), nothing happens.
     */
    fun requestDownload() {
        if (isUpdateAvailable && !isDownloading) {
            isDownloading = true
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                try {
                    updater.requestDownload()
                }
                catch (e: Exception) {
                    Log.e("Updates", "Cannot download new update: ${e.message}")
                }
            }
        }
    }

}
