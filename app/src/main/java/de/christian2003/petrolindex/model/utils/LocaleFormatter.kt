package de.christian2003.petrolindex.model.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class LocaleFormatter {

    companion object {

        /**
         * Method formats the passed epoch second to the date format of the current locale.
         *
         * @param epochSecond   Epoch second to format.
         * @return              Formatted date.
         */
        fun epochSecondToFormattedDate(epochSecond: Long): String {
            val localDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
            val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val formattedDate = localDateTime.format(dateTimeFormatter)
            return formattedDate
        }


        /**
         * Method formats the passed amount of cents (e.g. euro cents or dollar cents) to the currency
         * format of the current locale (e.g. "1.005,44" or "1,005.44").
         *
         * @param cents Amount of cents to format.
         * @return      Formatted currency.
         */
        fun centsToFormattedCurrency(cents: Int): String {
            val numberFormat = DecimalFormat("#,###.00")
            val formattedNumber = numberFormat.format(cents / 100.0)
            return formattedNumber
        }


        /**
         * Method formats the passed amount of milliliters to an amount of liters.
         *
         * @param milliliters   Milliliters to format.
         * @return              Formatted liters.
         */
        fun millilitersToFormattedLiters(milliliters: Int): String {
            val numberFormat = DecimalFormat("#,###.00")
            val formattedNumber = numberFormat.format(milliliters / 100.0)
            return formattedNumber
        }

    }

}
