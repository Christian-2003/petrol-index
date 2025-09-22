package de.christian2003.petrolindex.plugin.presentation.view.analysis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.petrolindex.application.analysis.AnalysisUseCase
import de.christian2003.petrolindex.domain.analysis.AnalysisPrecision
import de.christian2003.petrolindex.domain.analysis.AnalysisResult
import de.christian2003.petrolindex.domain.analysis.AnalysisResultCluster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * View model for the screen which displays the analysis.
 */
class AnalysisViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Use case to start an analysis.
     */
    private lateinit var analysisUseCase: AnalysisUseCase

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Analysis period selected by the user.
     */
    lateinit var analysisPeriod: AnalysisPeriod

    /**
     * Analysis result. This is null while the analysis is running.
     */
    var analysisResult: AnalysisResult? by mutableStateOf(null)
        private set

    /**
     * Precision to which the analysis is finished.
     */
    var analysisPrecision: AnalysisPrecision by mutableStateOf(AnalysisPrecision.MONTH)
        private set

    /**
     * Cluster that is currently being displayed through the cluster sheet.
     */
    var clusterToShow: AnalysisResultCluster? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to pick the date range for the analysis is currently visible.
     */
    var isDatePickerDialogVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     *
     * @param analysisUseCase   Analysis use case.
     */
    fun init(analysisUseCase: AnalysisUseCase) {
        if (isInitialized) {
            return
        }
        this.analysisUseCase = analysisUseCase
        startAnalysis(AnalysisPeriod.CURRENT_YEAR, true)
        isInitialized = true
    }


    /**
     * Starts the analysis for the specified period. This function only starts the analysis if there
     * is not analysis currently running. Pass force=true to override this behaviour and always start
     * an analysis when calling.
     *
     * @param analysisPeriod    Period for which to start the analysis.
     * @param force             Whether to force-start an analysis regardless of whether one is
     *                          currently running.
     */
    fun startAnalysis(analysisPeriod: AnalysisPeriod, force: Boolean = false) = viewModelScope.launch(Dispatchers.Main) {
        if (analysisResult != null || force) {
            this@AnalysisViewModel.analysisResult = null
            this@AnalysisViewModel.analysisPeriod = analysisPeriod

            val periodLength: Long = analysisPeriod.end.toEpochDay() - analysisPeriod.start.toEpochDay()
            this@AnalysisViewModel.analysisPrecision = if (periodLength <= 365) {
                AnalysisPrecision.MONTH //0 months - 12 months
            } else if (periodLength <= 1825) {
                AnalysisPrecision.QUARTER //13 months - 5 years
            } else {
                AnalysisPrecision.YEAR //More than 5 years
            }

            val result: AnalysisResult = analysisUseCase.analyzeData(
                start = analysisPeriod.start,
                end = analysisPeriod.end,
                precision = this@AnalysisViewModel.analysisPrecision
            )

            this@AnalysisViewModel.analysisPrecision = result.metadata.precision
            this@AnalysisViewModel.analysisResult = result
        }
    }

}
