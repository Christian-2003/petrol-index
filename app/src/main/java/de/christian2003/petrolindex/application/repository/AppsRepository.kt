package de.christian2003.petrolindex.application.repository

import de.christian2003.petrolindex.domain.apps.AppItem


/**
 * Repository accesses the apps that should be advertised to the user.
 */
interface AppsRepository {

    /**
     * Returns a list of all apps to advertise.
     *
     * @return  List of all apps.
     */
    suspend fun getApps(): List<AppItem>

}
