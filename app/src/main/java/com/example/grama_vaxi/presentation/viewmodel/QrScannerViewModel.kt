package com.example.grama_vaxi.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.repository.AlertRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class QrScannerUiState(
    val isScanning: Boolean = false,
    val error: String? = null,
    val scannedPayload: String? = null
)

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    fun scanImage(context: Context, uri: Uri, ownerUid: String, onScanned: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, error = null)
            try {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { 
                    BitmapFactory.decodeStream(it) 
                } ?: throw Exception("Failed to load image")

                val image = InputImage.fromBitmap(bitmap, 0)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                val barcodes = scanner.process(image).await()
                if (barcodes.isNotEmpty()) {
                    val payload = barcodes[0].rawValue ?: throw Exception("Empty QR code")
                    saveAlertAndFinish(ownerUid, payload, onScanned)
                } else {
                    _uiState.value = _uiState.value.copy(isScanning = false, error = "No QR code found in image")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isScanning = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun saveAlertAndFinish(ownerUid: String, payload: String, onScanned: (String) -> Unit) {
        viewModelScope.launch {
            alertRepository.saveScannedAlert(ownerUid, payload)
            onScanned(payload)
        }
    }
}
