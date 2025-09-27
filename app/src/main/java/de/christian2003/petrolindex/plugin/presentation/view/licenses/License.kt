package de.christian2003.petrolindex.plugin.presentation.view.licenses

/**
 * Class models license for a software used by the app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
data class License (

    /**
     * Attribute stores the name of the software.
     */
    val softwareName: String,

    /**
     * Attribute stores the resource file of the license.
     */
    val licenseFile: String

)