package com.weathersnap.ui.report

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.repository.ReportRepository
import com.weathersnap.domain.model.Report
import com.weathersnap.util.CompressedImageResult
import com.weathersnap.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SaveUiState {
    data object Idle : SaveUiState()
    data object Saving : SaveUiState()
    data object Saved : SaveUiState()
    data class Error(val message: String) : SaveUiState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val imageCompressor: ImageCompressor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val reports: StateFlow<List<Report>> = reportRepository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportCount: StateFlow<Int> = reportRepository.getReportCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    // Draft state preserved across process death via SavedStateHandle
    val draftImagePath: StateFlow<String?> = savedStateHandle.getStateFlow("draftImagePath", null)
    val draftOriginalSize: StateFlow<Long> = savedStateHandle.getStateFlow("draftOriginalSize", 0L)
    val draftCompressedSize: StateFlow<Long> = savedStateHandle.getStateFlow("draftCompressedSize", 0L)
    val draftNotes: StateFlow<String> = savedStateHandle.getStateFlow("draftNotes", "")

    // Temp file path (original uncompressed) to clean up
    private var tempImagePath: String? = null

    fun onNotesChanged(notes: String) {
        savedStateHandle["draftNotes"] = notes
    }

    fun processImage(context: Context, originalPath: String) {
        viewModelScope.launch {
            try {
                tempImagePath = originalPath
                val result: CompressedImageResult = imageCompressor.compressImage(context, originalPath)
                savedStateHandle["draftImagePath"] = result.compressedPath
                savedStateHandle["draftOriginalSize"] = result.originalSize
                savedStateHandle["draftCompressedSize"] = result.compressedSize

                // Delete the temp original file
                imageCompressor.deleteTempFile(originalPath)
                tempImagePath = null
            } catch (e: Exception) {
                // Keep original if compression fails
                savedStateHandle["draftImagePath"] = originalPath
            }
        }
    }

    fun saveReport(
        cityName: String,
        temperature: Double,
        condition: String,
        humidity: Int,
        windSpeed: Double,
        pressure: Double
    ) {
        val imagePath = draftImagePath.value
        if (imagePath.isNullOrEmpty()) return

        viewModelScope.launch {
            _saveState.value = SaveUiState.Saving
            try {
                val report = Report(
                    cityName = cityName,
                    temperature = temperature,
                    condition = condition,
                    humidity = humidity,
                    windSpeed = windSpeed,
                    pressure = pressure,
                    imagePath = imagePath,
                    originalImageSize = draftOriginalSize.value,
                    compressedImageSize = draftCompressedSize.value,
                    notes = draftNotes.value,
                    timestamp = System.currentTimeMillis()
                )
                reportRepository.saveReport(report)
                clearDraft()
                _saveState.value = SaveUiState.Saved
            } catch (e: Exception) {
                _saveState.value = SaveUiState.Error(
                    e.message ?: "Failed to save report"
                )
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveUiState.Idle
    }

    private fun clearDraft() {
        savedStateHandle["draftImagePath"] = null
        savedStateHandle["draftOriginalSize"] = 0L
        savedStateHandle["draftCompressedSize"] = 0L
        savedStateHandle["draftNotes"] = ""
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up temp files if report was not saved
        tempImagePath?.let { imageCompressor.deleteTempFile(it) }
    }
}
