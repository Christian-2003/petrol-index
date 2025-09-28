package de.christian2003.petrolindex.domain.apps

import android.net.Uri


/**
 * Models an app that the user can check out as well. Typically, these are other apps developed by
 * Christian-2003 that should be advertised in Petrol Index.
 *
 * @param packageName   Package name of the app, e.g. "de.christian2003.petrolindex".
 * @param url           URL to the website for the app, e.g. "https://christian2003.de/petrolindex".
 * @param displayName   App name displayed to the user, e.g. "Petrol Index".
 * @param iconUrl       URL to the SVG displaying the app icon.
 */
data class AppItem(
    val packageName: String,
    val url: Uri,
    val displayName: String,
    val iconUrl: Uri
)
