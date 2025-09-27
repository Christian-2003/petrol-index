package de.christian2003.petrolindex.plugin

import android.app.Application
import de.christian2003.petrolindex.plugin.infrastructure.rest.HttpClientProvider
import okhttp3.OkHttpClient


/**
 * Application class for Petrol Index.
 */
class PetrolIndexApplication(): Application() {

    /**
     * Stores the OkHttpClient to use for REST requests in the app.
     */
    private var client: OkHttpClient? = null


    /**
     * Returns the OkHttpClient to use for all web requests.
     *
     * @return  OkHttpClient.
     */
    fun getClient(): OkHttpClient {
        if (client == null) {
            client = HttpClientProvider().provideOkHttpClient(this)
        }
        return client!!
    }

}
