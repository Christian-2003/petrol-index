package de.christian2003.petrolindex.application.services

import org.junit.Assert
import org.junit.Before
import org.junit.Test


class CurrencyFormatterServiceUnitTest {

    private lateinit var service: CurrencyFormatterService


    @Before
    fun setup() {
        service = CurrencyFormatterService()
    }


    @Test
    fun singleDigitValue() {
        Assert.assertEquals("1.00", service.format(100))
        Assert.assertEquals("1.20", service.format(120))
        Assert.assertEquals("1.23", service.format(123))
        Assert.assertEquals("1.02", service.format(102))
    }


    @Test
    fun tripleDigitValue() {
        Assert.assertEquals("123.00", service.format(12300))
        Assert.assertEquals("123.40", service.format(12340))
        Assert.assertEquals("123.45", service.format(12345))
        Assert.assertEquals("123.04", service.format(12304))
    }


    @Test
    fun quadrupleDigitValue() {
        Assert.assertEquals("1,234.00", service.format(123400))
        Assert.assertEquals("1,234.50", service.format(123450))
        Assert.assertEquals("1,234.56", service.format(123456))
        Assert.assertEquals("1,234.05", service.format(123405))
    }


    @Test
    fun emptyValue() {
        Assert.assertEquals("0.00", service.format(0))
    }

}
