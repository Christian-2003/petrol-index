package de.christian2003.petrolindex.domain.analysis

import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class AnalysisResultClusterUnitTest {

    @Test
    fun createAnalysisResultCluster() {
        AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 350.5,
            totalAverage = 30.2,
            precisionAverage = 10.8,
            type = AnalysisResultClusterType.VOLUME
        )
    }


    @Test
    fun createAnalysisResultClusterWithNoTotalSum() {
        AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 0.0,
            totalAverage = 30.2,
            precisionAverage = 10.8,
            type = AnalysisResultClusterType.VOLUME
        )
    }


    @Test
    fun createAnalysisResultClusterWithNoTotalAverage() {
        AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 350.5,
            totalAverage = 0.0,
            precisionAverage = 10.8,
            type = AnalysisResultClusterType.VOLUME
        )
    }


    @Test
    fun createAnalysisResultClusterWithNoPrecisionAverage() {
        AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 350.5,
            totalAverage = 30.2,
            precisionAverage = 0.0,
            type = AnalysisResultClusterType.VOLUME
        )
    }


    @Test
    fun createAnalysisResultClusterWithNegativeTotalSum() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultCluster(
                sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                totalSum = -1.0,
                totalAverage = 30.2,
                precisionAverage = 10.8,
                type = AnalysisResultClusterType.VOLUME
            )
        }
    }


    @Test
    fun createAnalysisResultClusterWithNegativeTotalAverage() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultCluster(
                sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                totalSum = 350.5,
                totalAverage = -1.0,
                precisionAverage = 10.8,
                type = AnalysisResultClusterType.VOLUME
            )
        }
    }


    @Test
    fun createAnalysisResultClusterWithNegativePrecisionAverage() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AnalysisResultCluster(
                sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
                totalSum = 350.5,
                totalAverage = 30.2,
                precisionAverage = -1.0,
                type = AnalysisResultClusterType.VOLUME
            )
        }
    }


    @Test
    fun equalsAndHashCode() {
        val cluster1 = AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 350.5,
            totalAverage = 30.2,
            precisionAverage = 10.8,
            type = AnalysisResultClusterType.VOLUME
        )
        val cluster2 = AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 350.5,
            totalAverage = 30.2,
            precisionAverage = 10.8,
            type = AnalysisResultClusterType.VOLUME
        )
        val cluster3 = AnalysisResultCluster(
            sumDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            cumulatedDiagram = AnalysisDiagram.Builder(LocalDate.now()).build(),
            totalSum = 150.1,
            totalAverage = 10.9,
            precisionAverage = 5.7,
            type = AnalysisResultClusterType.TOTAL_PRICE
        )

        Assert.assertEquals(true, cluster1 == cluster2)
        Assert.assertEquals(false, cluster1 == cluster3)
        Assert.assertEquals(cluster1.hashCode(), cluster2.hashCode())
        Assert.assertNotEquals(cluster1.hashCode(), cluster3.hashCode())
    }

}
