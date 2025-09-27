package de.christian2003.petrolindex.application.apps

import de.christian2003.petrolindex.application.repository.AppsRepository
import de.christian2003.petrolindex.domain.apps.AppItem


/**
 * Use case through which to query all apps.
 */
class GetAppsUseCase(
    private val repository: AppsRepository
) {

    /**
     * Returns a list of all apps.
     *
     * @return  List of all apps.
     */
    suspend fun getAllApps(): List<AppItem> {
        return repository.getApps()
    }

}
