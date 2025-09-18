package de.christian2003.petrolindex.application.services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * Service can format local dates.
 */
class DateTimeFormatterService {

    /**
     * Date time format to use for formatting.
     */
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)


    /**
     * Formats the local date instance passed as argument according to the locale format.
     *
     * @param value Local date to format.
     * @return      Formatted local date.
     */
    fun format(value: LocalDate): String {
        return value.format(dateTimeFormat)
    }

}
