package de.christian2003.petrolindex.plugin.presentation.view.help

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel


/**
 * View model for the screen displaying all help messages.
 */
class HelpViewModel(application: Application): AndroidViewModel(application) {

    private var isInitialized: Boolean = false


    /**
     * Maps each help card to a mutable state which indicates whether the respective help message
     * is visible.
     */
    val helpCards: MutableMap<HelpCard, Boolean> = mutableStateMapOf()


    /**
     * Initializes the view model.
     */
    fun init() {
        if (isInitialized) {
            return
        }
        val context: Context = getApplication<Application>().baseContext
        HelpCard.entries.forEach { helpCard ->
            helpCards[helpCard] = helpCard.getVisible(context)
        }
        isInitialized = true
    }


    /**
     * Toggles the visibility of a help card.
     *
     * @param helpCard  Help card whose visibility to toggle.
     */
    fun toggleHelpCardVisibility(helpCard: HelpCard) {
        val context: Context = getApplication<Application>().baseContext
        helpCard.setVisible(context, !helpCard.getVisible(context))
        helpCards[helpCard] = helpCard.getVisible(context)
    }


    /**
     * Dismisses the help card on the page.
     */
    fun dismissHelpCard() {
        val context: Context = getApplication<Application>().baseContext
        HelpCard.HELP_LIST.setVisible(context, false)
        helpCards[HelpCard.HELP_LIST] = false
    }

}
