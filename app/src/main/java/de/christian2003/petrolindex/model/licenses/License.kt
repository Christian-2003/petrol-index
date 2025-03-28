package de.christian2003.petrolindex.model.licenses


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
     * Attribute stores the version of the software.
     */
    val softwareVersion: String,

    /**
     * Attribute stores the name of the license.
     */
    val licenseName: String,

    /**
     * Attribute stores the resource file of the license.
     */
    val licenseFile: String

)
