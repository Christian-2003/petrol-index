package de.christian2003.petrolindex.domain.apps

import android.net.Uri


/**
 * Models an app that the user can check out as well. Typically, these are other apps developed by
 * Christian-2003 that should be advertised in Petrol Index.
 */
class AppItem(

    /**
     * Package name of the app, e.g. "de.christian2003.petrolindex".
     */
    val packageName: String,

    /**
     * URL to the website for the app, e.g. "https://christian2003.de/petrolindex".
     */
    val url: Uri,

    /**
     * App name displayed to the user, e.g. "Petrol Index".
     */
    val displayName: String,

    /**
     * URL to the SVG displaying the app icon.
     */
    val iconUrl: Uri

)
