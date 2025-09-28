package de.christian2003.petrolindex.domain.analysis

import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisDiagramUnitTest {

    @Test
    fun createDiagram() {
        AnalysisDiagram(
            start = LocalDate.now(),
            values = listOf(1.0, 5.5, 2.3, 10.9),
            min = 1.0,
            max = 10.9
        )
    }


    @Test
    fun createDiagramWithMinAfterMax() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisDiagram(
                start = LocalDate.now(),
                values = listOf(1.0, 5.5, 2.3, 10.9),
                min = 10.9,
                max = 5.5
            )
        }
    }


    @Test
    fun createWithBuilder() {
        val date = LocalDate.now()
        val builder = AnalysisDiagram.Builder(date)
        builder.addValue(1.0)
        builder.addValue(5.5)
        builder.addValue(2.3)
        builder.addValue(10.9)
        val diagram = builder.build()

        Assert.assertEquals(4, diagram.values.size)
        Assert.assertEquals(1.0, diagram.values[0], 0.0)
        Assert.assertEquals(5.5, diagram.values[1], 0.0)
        Assert.assertEquals(2.3, diagram.values[2], 0.0)
        Assert.assertEquals(10.9, diagram.values[3], 0.0)
        Assert.assertEquals(1.0, diagram.min, 0.0)
        Assert.assertEquals(10.9, diagram.max, 0.0)
        Assert.assertEquals(date, diagram.start)
    }

}
