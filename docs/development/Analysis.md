<img src="../img/icon.png" height="150" align="right">

# Analysis
This document describes the inner workings of the analysis.

###### Table of Contents
1. [Workflow](#workflow)
2. [Summarize All Data](#summarize-all-data)
3. [Convert Summarized Data to Data Lines](#convert-summarized-data-to-data-lines)
4. [Calculate Cumulated Values](#calculate-cumulated-values)
5. [Interpolate Missing Values](#interpolate-missing-values)
6. [Generate Analysis Result](#generate-analysis-result)

<br/>

## Workflow
In general, the analysis can be described through the following simple workflow:

![](../img/development/workflow_analysis.drawio.svg)

The individual steps of this workflow are explained in detail below.

<br/>

## Summarize All Data
The first step of the analysis is performed by the `AnalysisDataSummarizer`-class, which summarizes a list of consumptions.

![](../img/development/workflow_analysis_summarizer.drawio.svg)

Based on the analysis precision (Month, Quarter or Year), the data is grouped by month, quarter or year, which creates a `Map<LocalDate, List<Consumption>>`.

The summarizer iterates through the resulting `Map` and calculates the following values for each month: "Volume", "Total price", "Distance traveled" and "Price per liter". The result is stored in the following data class:

```Kotlin
data class AnalysisDataSummary(
    val date: LocalDate,
    val volumeSum: Int,
    val totalPriceSum: Int,
    val distanceTraveledSum: Int,
    val volumeCount: Int,
    val totalPriceCount: Int,
    val distanceTraveledCount: Int,
    val pricePerLiterAverage: Int
)
```

Lastly, the resulting list of `AnalysisDataSummary`-instances is sorted based on their `date`-property.

<br/>

## Convert Summarized Data to Data Lines
After the data has been [summarized](#summarize-all-data), the resulting list of `AnalysisDataSummary`-instances needs to be converted to data lines. This is done through the `AnalysisDataSummariesToDataLines`-class.

The algorithm converts the list of summaries into multiple lists of `AnalysisDataPoint`-instances. These instances represent a single data point for a diagram and are modeled as follows:

```Kotlin
data class AnalysisDataPoint(
    val date: LocalDate,
    val value: Int
)
```

The class generates a DTO object that contains lists of these data points for the following data: "Volume", "Total price", "Distance traveled", "Price per liter":

```Kotlin
data class AnalysisDataLines(
    val volumes: List<AnalysisDataPoint>,
    val totalPrices: List<AnalysisDataPoint>,
    val distancesTraveled: List<AnalysisDataPoint>,
    val pricePerLiter: List<AnalysisDataPoint>,
    val volumesCount: Int,
    val totalPricesCount: Int,
    val distancesTraveledCount: Int
)
```

The properties `volumesCount`, `totalPricesCount` and `distancesTraveledCount` are the sums of the respective values from the `AnalysisDataSummary`-instances.

<br/>

## Calculate Cumulated Values
Each [data line](#convert-summarized-data-to-data-lines) is now used to calculate cumulated values for the data line. This is done through the `AnalysisCumulationCalculator`.

This class calculates cumulated values and returns a list of `AnalysisDataPoint`-instances.

<br/>

## Interpolate Missing Values
The values retrieved from the `AnalysisDataSummariesToDataLines`-class, as well as the cumulated values might miss some values in between months, quarters or years. These values can be interpolated using the `AnalysisDataInterpolator`.

![](../img/development/workflow_analysis_interpolator.drawio.svg)

The algorithm iterates through every item of the `AnalysisDataPoint`-list. For each item, the algorithm checks whether values are missing until the next item. If this is the case, the interpolated values are calculated and inserted into the list.

Depending on the interpolation mode, the following rules apply:
* **Interpolate:** The average values in between the original values are calculated. This creates a linear correlation in between the two original values and the interpolated values. This mode is used when interpolating the [cumulated values](#calculate-cumulated-values).
* **Fill with '0':** The interpolator uses '0' as interpolated value. This mode is used when interpolating the original [data lines](#convert-summarized-data-to-data-lines).

The algorithm returns a list of `AnalysisDataPoint`-instances.

<br/>

## Generate Analysis Result
Lastly, the values that were generated beforehand are converted into an instance of `AnalysisResult`, which is modeled as follows:

```Kotlin
data class AnalysisResult(
    val volume: AnalysisResultCluster,
    val totalPrice: AnalysisResultCluster,
    val distanceTraveled: AnalysisResultCluster,
    val pricePerLiterDiagram: AnalysisDiagram,
    val metadata: AnalysisResultMetadata,
)

data class AnalysisResultCluster(
    val sumDiagram: AnalysisDiagram,
    val cumulatedDiagram: AnalysisDiagram,
    val totalSum: Double,
    val totalAverage: Double,
    val precisionAverage: Double,
    val type: AnalysisResultClusterType
)

data class AnalysisDiagram(
    val start: LocalDate,
    val values: List<Double>,
    val min: Double,
    val max: Double
)
```

The `AnalysisDiagram` models a single diagram and contains all data that is required to display the diagram using the "ComposeCharts"-library. This includes a value of doubles as well as a start date from which the diagram labels are calculated.

The entire result is grouped into clusters. The data for "Volume", "Total price" and "Distance traveled" is grouped using such a cluster. A cluster contains a regular diagram as well as a cumulated diagram.

The generation of the `AnalysisResult` is handled by the `ExtensiveAnalysisUseCase`-class, which orchestrates the entire analysis and provides methods to convert the data from the previous steps into the result.

<br/>

***

2025-09-28  
&copy; Christian-2003
