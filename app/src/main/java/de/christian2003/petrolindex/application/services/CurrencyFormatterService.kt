package de.christian2003.petrolindex.application.services

import java.text.DecimalFormat
import java.text.NumberFormat


/**
 * Service can format currencies.
 */
class CurrencyFormatterService {

    /**
     * Number format to use for formatting.
     */
    private val numberFormat: NumberFormat = DecimalFormat("#,##0.00")


    /**
     * The value passed in cents (e.g. 123,456 cents) is converted to euros or dollars and formatted
     * according to the local number format (e.g. to "1,234.56").
     *
     * @param value Value (in cents) to format.
     * @return      Formatted value (in euros or dollars).
     */
    fun format(value: Int): String {
        val formattedNumber: String = numberFormat.format(value.toDouble() / 100.0)
        return formattedNumber
    }


    /**
     * The value passed in euros or dollars (e.g. 1234.56) is formatted according to the local
     * number format (e.g. "1,234.56").
     *
     * @param value Value (in euros) to format.
     * @return      Formatted value (in euros or dollars).
     */
    fun format(value: Double): String {
        val formattedNumber = numberFormat.format(value)
        return formattedNumber
    }

}
