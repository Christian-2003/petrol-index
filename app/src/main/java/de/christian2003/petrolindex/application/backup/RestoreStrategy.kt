package de.christian2003.petrolindex.application.backup


/**
 * Fields describe the strategy for importing a backup into the app.
 */
enum class RestoreStrategy {

    DELETE_EXISTING_DATA,

    REPLACE_EXISTING_DATA,

    IGNORE_EXISTING_DATA

}
