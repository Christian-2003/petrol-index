package de.christian2003.petrolindex.plugin.infrastructure.db.converter

import androidx.room.TypeConverter
import java.time.LocalDate


/**
 * Converter for Room database to convert a local date field into a long and vice versa.
 */
class LocalDateConverter {

    /**
     * Converts the passed local date into a long.
     *
     * @param value Local date to convert into a long.
     * @return      Long converted from the passed local date.
     */
    @TypeConverter
    fun fromLocalDate(value: LocalDate): Long {
        return value.toEpochDay()
    }


    /**
     * Converts the long representing a local date into a LocalDate instance.
     *
     * @param value Long to convert into a local date.
     * @return      Local date converted from the passed long.
     */
    @TypeConverter
    fun toLocalDate(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }

}
