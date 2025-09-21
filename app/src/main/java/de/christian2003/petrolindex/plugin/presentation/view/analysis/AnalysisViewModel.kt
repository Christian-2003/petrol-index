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
import java.time.LocalDate

class AnalysisViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var analysisUseCase: AnalysisUseCase

    private var isInitialized: Boolean = false

    var analysisResult: AnalysisResult? by mutableStateOf(null)
        private set

    var analysisPrecision: AnalysisPrecision by mutableStateOf(AnalysisPrecision.MONTH)
        private set

    var clusterToShow: AnalysisResultCluster? by mutableStateOf(null)


    fun init(analysisUseCase: AnalysisUseCase) {
        if (isInitialized) {
            return
        }
        this.analysisUseCase = analysisUseCase
        startAnalysis()
        isInitialized = true
    }


    fun startAnalysis() = viewModelScope.launch(Dispatchers.Main) {
        this@AnalysisViewModel.analysisResult = null
        val currentDate: LocalDate = LocalDate.now()
        val result: AnalysisResult = analysisUseCase.analyzeData(currentDate.minusYears(1), currentDate, analysisPrecision)
        this@AnalysisViewModel.analysisResult = result
    }

}
