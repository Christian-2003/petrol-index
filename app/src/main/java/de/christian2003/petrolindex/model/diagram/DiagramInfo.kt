package de.christian2003.petrolindex.model.diagram

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import de.christian2003.petrolindex.R
import de.christian2003.petrolindex.database.PetrolEntry
import java.util.Locale


/**
 * Data class contains all information required for a single diagram for petrol entries.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
class DiagramInfo(

    /**
     * Attribute stores the data to display.
     */
    val data: List<Double>,

    /**
     * Attribute stores the title for the diagram.
     */
    val title: String,

    /**
     * Attribute stores the unit of the data.
     */
    val unit: String,

    /**
     * Attribute stores the label for the indicators, where "{arg}" can be replaced with a value.
     */
    val labelIndicator: String,

    /**
     * Attribute stores the color for the diagram.
     */
    val color: Color,

    /**
     * Attribute stores the total value.
     */
    val totalValue: Double,

    /**
     * Attribute stores the type of the diagram.
     */
    val type: DiagramType

) {

    /**
     * Method returns the a formatted string containing the total value.
     *
     * @param locale    Locale to use for formatting.
     * @return          Formatted string.
     */
    fun getFormattedTotalValue(locale: Locale): String {
        return formatValue(locale, totalValue)
    }

    /**
     * Method returns a formatted string containing the value specified. The string is formatted
     * according to the format used with the diagram type.
     *
     * @param locale    Locale to use for formatting.
     * @param value     Value to format.
     * @return          Formatted string.
     */
    fun formatValue(locale: Locale, value: Double): String {
        return when (type) {
            DiagramType.PRICE_PER_LITER,
            DiagramType.CUMULATED_VOLUME,
            DiagramType.CUMULATED_EXPENSES
            -> String.format(locale, "%.2f", value)
            DiagramType.DISTANCE -> value.toInt().toString()
        }
    }


    companion object {

        /**
         * Method creates a DiagramInfo instance for the parameter specified.
         *
         * @param petrolEntries     List of petrol entries for which to create an instance.
         * @param context           Context to create the instance.
         * @param colorScheme       Color scheme.
         * @param type              Type of the diagram info to create.
         */
        fun createInstance(
            petrolEntries: List<PetrolEntry>,
            context: Context,
            colorScheme: ColorScheme,
            type: DiagramType
        ): DiagramInfo {
            return when (type) {
                DiagramType.PRICE_PER_LITER -> {
                    //Price per liter:
                    var averagePricePerLiter = 0.0
                    petrolEntries.forEach {
                        averagePricePerLiter += it.getPricePerLiter()
                    }
                    averagePricePerLiter = (averagePricePerLiter / petrolEntries.size).toDouble() / 100.0
                    DiagramInfo(
                        data = petrolEntries.asReversed().map { it.getPricePerLiter().toDouble() / 100.0 },
                        title = context.getString(R.string.main_diagram_price_per_liter_title),
                        unit = context.getString(R.string.main_diagram_price_per_liter_unit),
                        labelIndicator = context.getString(R.string.main_diagram_price_per_liter_indicator),
                        color = colorScheme.primary,
                        totalValue = averagePricePerLiter,
                        type = type
                    )
                }
                DiagramType.DISTANCE -> {
                    //Distance:
                    var totalDistance = 0
                    val distances: MutableList<Double> = mutableListOf()
                    petrolEntries.asReversed().forEach { petrolEntry ->
                        if (petrolEntry.distanceTraveled != null) {
                            distances.add(petrolEntry.distanceTraveled.toDouble())
                            totalDistance += petrolEntry.distanceTraveled
                        }
                    }
                    DiagramInfo(
                        data = distances,
                        title = context.getString(R.string.main_diagram_distance_title),
                        unit = context.getString(R.string.main_diagram_distance_unit),
                        labelIndicator = context.getString(R.string.main_diagram_distance_indicator),
                        color = colorScheme.secondary,
                        totalValue = totalDistance.toDouble(),
                        type = type
                    )
                }
                DiagramType.CUMULATED_EXPENSES -> {
                    //Cumulated expenses:
                    var sum = 0
                    val prices: List<Double> = petrolEntries.asReversed().map {
                        sum += it.totalPrice
                        sum.toDouble() / 100.0
                    }
                    DiagramInfo(
                        data = prices,
                        title = context.getString(R.string.main_diagram_cumulated_expenses_title),
                        unit = context.getString(R.string.main_diagram_cumulated_expenses_unit),
                        labelIndicator = context.getString(R.string.main_diagram_cumulated_expenses_indicator),
                        color = colorScheme.tertiary,
                        totalValue = sum.toDouble() / 100.0,
                        type = type
                    )
                }
                DiagramType.CUMULATED_VOLUME -> {
                    //Cumulated volume:
                    var sum = 0
                    val volumes: List<Double> = petrolEntries.asReversed().map {
                        sum += it.volume
                        sum.toDouble() / 100.0
                    }
                    DiagramInfo(
                        data = volumes,
                        title = context.getString(R.string.main_diagram_cumulated_volume_title),
                        unit = context.getString(R.string.main_diagram_cumulated_volume_unit),
                        labelIndicator = context.getString(R.string.main_diagram_cumulated_volume_indicator),
                        color = colorScheme.tertiary,
                        totalValue = sum.toDouble() / 100.0,
                        type = type
                    )
                }
            }
        }

    }

}
