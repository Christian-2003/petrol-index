package de.christian2003.petrolindex.application.services

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate


class DateTimeFormatterServiceUnitTest {

    private lateinit var service: DateTimeFormatterService


    @Before
    fun setup() {
        service = DateTimeFormatterService()
    }


    @Test
    fun format() {
        val date: LocalDate = LocalDate.of(2025, 9, 19)
        Assert.assertEquals("Sep 19, 2025", service.format(date))
    }

}
