package de.christian2003.petrolindex.model.diagram


/**
 * Enum contains the types for all diagrams that can be displayed for the petrol entries.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */

enum class DiagramType {

    /**
     * Type for the diagram displaying the price per liter over time.
     */
    PRICE_PER_LITER,

    /**
     * Type for the diagram displaying the distance traveled.
     */
    DISTANCE,

    /**
     * Type for the diagram displaying the cumulated expenses.
     */
    CUMULATED_EXPENSES,

    /**
     * Type for the diagram displaying the cumulated volume.
     */
    CUMULATED_VOLUME

}
