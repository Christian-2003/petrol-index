package de.christian2003.petrolindex.plugin.infrastructure.backup


/**
 * Provider for app information that should be included in JSON backups.
 */
interface AppInfoProvider {

    /**
     * Returns the app name, e.g. "Petrol Index".
     *
     * @return  App name.
     */
    fun getAppName(): String


    /**
     * Returns the app version name, e.g. "1.2.3".
     *
     * @return  App version name.
     */
    fun getAppVersion(): String

}
