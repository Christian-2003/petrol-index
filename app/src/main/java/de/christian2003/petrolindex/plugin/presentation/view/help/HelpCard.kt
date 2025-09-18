package de.christian2003.petrolindex.plugin.presentation.view.help

import android.content.Context
import androidx.core.content.edit


/**
 * Fields store all help cards that are available in the app.
 *
 * @param sharedPreferencesKey  Key with which to store the help card in shared preferences.
 */
enum class HelpCard(
    private val sharedPreferencesKey: String
) {

    CONSUMPTION("help_consumption"),

    HELP_LIST("help_list");


    /**
     * Returns whether the help card is visible.
     *
     * @param context   Context for shared preferences.
     * @return          Whether help card is visible.
     */
    fun getVisible(context: Context): Boolean {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(sharedPreferencesKey, true)
    }


    /**
     * Changes whether the help card is visible.
     *
     * @param context   Context for shared preferences.
     * @param isVisible Whether the help card should be visible.
     */
    fun setVisible(context: Context, isVisible: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
            putBoolean(sharedPreferencesKey, isVisible)
        }
    }

}
